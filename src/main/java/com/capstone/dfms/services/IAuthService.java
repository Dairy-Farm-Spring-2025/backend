package com.capstone.dfms.services;

import com.capstone.dfms.models.UserEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface IAuthService {
    UserEntity processGoogleLogin(OAuth2AuthenticationToken authentication);
}
