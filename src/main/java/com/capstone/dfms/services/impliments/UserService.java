package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.constants.ImageContants;
import com.capstone.dfms.components.events.MailEvent;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.TokenProvider;
import com.capstone.dfms.components.utils.PasswordUtils;
import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.models.TokenEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.UserStatus;
import com.capstone.dfms.repositories.IRoleRepository;
import com.capstone.dfms.repositories.ITokenRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.UserForgotPasswordRequest;
import com.capstone.dfms.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleRepository roleRepository;
    private final ITokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    AuthenticationManager authenticationManager;

    @Value("${app.fe.verify_url}")
    private String verifyUrl;

    @Value("${app.fe.forgot_password_url}")
    private String forgotPasswordUrl;

    @Override
    public void createAccount(UserEntity user) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(user.getEmail());

        if(userOptional.isPresent()) {
            if(user.getEmailVerified()){
                throw new AppException(HttpStatus.BAD_REQUEST,"Email already exists");
            }
        }
        String defaultPassword = PasswordUtils.generateRandomString(8);
        user.setEmailVerified(false);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setProfilePhoto(ImageContants.DEFAULT_AVATAR);
        user.setIsActive(true);
        user.setStatus(UserStatus.active);

        RoleEntity role = roleRepository.findById(user.getRoleId().getId()).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoleId(role);

        String employeeNumber = generateEmployeeNumberByRole(role.getId());
        user.setEmployeeNumber(employeeNumber);

         userRepository.save(user);

        sendVerifyMail(user);



    }

    private String generateEmployeeNumberByRole(Long roleId) {
        long count = userRepository.count();

        String employeeNumberPrefix = null;
        if (roleId == 3) {
            employeeNumberPrefix = "VE";
        } if (roleId == 4) {
            employeeNumberPrefix = "WO";
        } 
        return employeeNumberPrefix + String.format("%03d", count + 1);
    }
    @Override
    public void verify(Long userId, String token) {
        tokenProvider.validateToken(token);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with id : " + userId)
                );
        user.setEmailVerified(true);

        userRepository.save(user);
    }

    @Override
    public String refresh(String token) {
        TokenEntity refreshToken = tokenRepository.findByName(token)
                .orElseThrow(() ->
                        new AppException(HttpStatus.BAD_REQUEST,"Refresh Token not found with token : " + token)
                );
        if(refreshToken.isRevoked()){
            throw new AppException(HttpStatus.BAD_REQUEST,"Token đã bị thu hồi");
        }
        if(refreshToken.isExpired()){
            throw new AppException(HttpStatus.BAD_REQUEST,"Token đã hết hạn");
        }
        if(refreshToken.getExpirationDate().isBefore(LocalDate.now())){
            refreshToken.setExpired(true);
            throw new AppException(HttpStatus.BAD_REQUEST,"Token đã hết hạn");
        }

        String accessToken = tokenProvider.createAccessToken(refreshToken.getUser().getId());

        return accessToken;
    }

    @Override
    public void setPassword(Long userId, String token, UserForgotPasswordRequest request) {
        tokenProvider.validateToken(token);
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(()
                        -> new DataNotFoundException("User", "id", userId));
        if(request.getPassword().equals(request.getConfirmedPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        else{
            throw new AppException(HttpStatus.BAD_REQUEST, "Confirmed password is wrong");
        }
        userRepository.save(user);
    }

    @Override
    public void sendMailForgotPassword(String email) {
        UserEntity user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User", "Email", email));

        String token = tokenProvider.createToken(user.getId(), 3600000);
        String urlPattern = forgotPasswordUrl + "?userId={0}&token={1}";
        String url = MessageFormat.format(urlPattern, user.getId(), token);
        applicationEventPublisher.publishEvent(new MailEvent(this, user, url, "forgot"));
    }

    private void sendVerifyMail(UserEntity user) {
        String token = tokenProvider.createToken(user.getId(), 3600000);
        String urlPattern = verifyUrl + "?userId={0}&token={1}";
        String url = MessageFormat.format(urlPattern, user.getId(), token);
        applicationEventPublisher.publishEvent(new MailEvent(this, user, url, "verify"));
    }
}
