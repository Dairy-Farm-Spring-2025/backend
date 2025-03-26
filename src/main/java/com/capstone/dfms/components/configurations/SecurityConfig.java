package com.capstone.dfms.components.configurations;

import com.capstone.dfms.components.fitters.TokenAuthenticationFilter;
import com.capstone.dfms.components.securities.TokenProvider;
import com.capstone.dfms.components.securities.UserDetailService;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.services.IAuthService;
import com.capstone.dfms.services.impliments.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private IAuthService authService;
    @Autowired

    private TokenProvider tokenProvider;


    @Autowired
    @Qualifier("delegatedAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPoint))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/login/**", "/oauth2/**").permitAll()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorize"))
                        .redirectionEndpoint(redir -> redir.baseUri("/login/oauth2/code/*"))
                        .successHandler(oauth2SuccessHandler())
                        .failureHandler(oauth2FailureHandler()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            try {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                UserEntity user = authService.processGoogleLogin(oauthToken);

                String accessToken = tokenProvider.createAccessToken(user.getId());
                String refreshToken = tokenProvider.createRefreshToken(principal);
                String userId = String.valueOf(user.getId());
                String roleName = user.getRoleId() != null ? user.getRoleId().getName() : "UNKNOWN";
                String userName = user.getName() != null ? user.getName() : "UNKNOWN";

                String redirectUrl = String.format(
                        "http://localhost:5173/dairy?access_token=%s&refresh_token=%s&userId=%d&userName=%s&roleName=%s",
                        URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                        URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                        user.getId(),
                        URLEncoder.encode(userName, StandardCharsets.UTF_8),
                        URLEncoder.encode(roleName, StandardCharsets.UTF_8)
                );

                response.setStatus(HttpServletResponse.SC_FOUND);
                response.setHeader("Location", redirectUrl);
                response.getWriter().flush();
                response.sendRedirect(redirectUrl);

            } catch (Exception e) {
                String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                String errorRedirectUrl = "http://localhost:5173/dairy?error=" + errorMessage;

                response.setStatus(HttpServletResponse.SC_FOUND);
                response.setHeader("Location", errorRedirectUrl);
                response.getWriter().flush();
            }
        };
    }


    @Bean
    public AuthenticationFailureHandler oauth2FailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + exception.getMessage() + "\"}");
        };
    }
}
