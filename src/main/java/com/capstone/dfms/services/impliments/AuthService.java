package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.repositories.ITokenRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.services.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final ITokenRepository tokenRepository;

    private final IUserRepository userRepository;

    @Override
    public UserEntity processGoogleLogin(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        System.out.println("Email đăng nhập Google: " + email);
        Optional<UserEntity> existingUser = userRepository.findByEmail(email);
        if(existingUser.isEmpty()) {
            throw new RuntimeException("Email " + email + " không tồn tại trong hệ thống!");
        }

        UserEntity user = existingUser.get();

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản không hoạt động!");
        }
        return user;
    }
}
