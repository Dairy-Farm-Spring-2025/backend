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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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
                        .anyRequest().permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorize"))
                        .userInfoEndpoint(userInfo -> userInfo.userService(configOAuth2UserService()))
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
                System.out.println("Success handler invoked");
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
                UserEntity user = authService.processGoogleLogin(oauthToken);

                String accessToken = tokenProvider.createAccessToken(principal);
                String refreshToken = tokenProvider.createRefreshToken(principal);
                String userId = String.valueOf(user.getId());
                String roleName = user.getRoleId() != null ? user.getRoleId().getName() : "UNKNOWN";
                String userName = user.getName() != null ? user.getName() : "UNKNOWN";

                String platform = request.getParameter("platform");
                System.out.println("Platform: " + platform);

                String redirectUrl;
                if ("mobile".equalsIgnoreCase(platform)) {
                    redirectUrl = String.format(
                            "exp://127.0.0.1:19000/",
                            URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                            URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                            URLEncoder.encode(userId, StandardCharsets.UTF_8),
                            URLEncoder.encode(userName, StandardCharsets.UTF_8),
                            URLEncoder.encode(roleName, StandardCharsets.UTF_8)
                    );
                } else {
                    redirectUrl = String.format(
                            "https://dairyfarmfpt.website/login/oauth2/callback?access_token=%s&refresh_token=%s&userId=%s&userName=%s&roleName=%s",
                            URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
                            URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                            URLEncoder.encode(userId, StandardCharsets.UTF_8),
                            URLEncoder.encode(userName, StandardCharsets.UTF_8),
                            URLEncoder.encode(roleName, StandardCharsets.UTF_8)
                    );
                }

                System.out.println("Redirecting to: " + redirectUrl);
                response.sendRedirect(redirectUrl);
            } catch (OAuth2AuthenticationException e) {
                System.err.println("OAuth2 error in success handler: " + e.getMessage());
                String errorCode = e.getError().getErrorCode(); // user_not_found hoặc user_disabled
                String encodedErrorCode = URLEncoder.encode(errorCode, StandardCharsets.UTF_8);
                String errorRedirectUrl = "https://dairyfarmfpt.website/login/oauth2/callback?error=" + encodedErrorCode;
                System.out.println("Redirecting to: " + errorRedirectUrl);
                response.sendRedirect(errorRedirectUrl);
            } catch (Exception e) {
                System.err.println("Unexpected error in success handler: " + e.getMessage());
                e.printStackTrace();
                String errorRedirectUrl = "https://dairyfarmfpt.website/login/oauth2/callback?error=internal_error";
                System.out.println("Redirecting to: " + errorRedirectUrl);
                response.sendRedirect(errorRedirectUrl);
            }
        };
    }


    @Bean
    public AuthenticationFailureHandler oauth2FailureHandler() {
        return (request, response, exception) -> {
            try {
                System.out.println("Failure handler invoked: " + exception.getMessage());
                String errorCode = "authentication_failed";
                if (exception instanceof OAuth2AuthenticationException) {
                    OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
                    errorCode = error.getErrorCode();
                }
                String encodedErrorCode = URLEncoder.encode(errorCode, StandardCharsets.UTF_8);
                String errorRedirectUrl = "http://localhost:5173/login/oauth2/callback?error=" + encodedErrorCode;
                System.out.println("Redirecting to: " + errorRedirectUrl);
                response.sendRedirect(errorRedirectUrl);
            } catch (Exception e) {
                System.err.println("Error in failure handler: " + e.getMessage());
                String fallbackErrorUrl = "http://localhost:5173/login/oauth2/callback?error=internal_error";
                System.out.println("Redirecting to: " + fallbackErrorUrl);
                response.sendRedirect(fallbackErrorUrl);
            }
        };
    }


    @Bean
    public ConfigOAuth2UserService configOAuth2UserService() {
        return new ConfigOAuth2UserService();
    }
}
