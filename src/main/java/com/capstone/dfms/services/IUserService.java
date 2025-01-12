package com.capstone.dfms.services;

import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.requests.UserChangePasswordRequest;
import com.capstone.dfms.requests.UserForgotPasswordRequest;
import com.capstone.dfms.requests.UserSignInRequest;
import com.capstone.dfms.responses.SignInResponse;

public interface IUserService {
    void createAccount(UserEntity user);

    SignInResponse signIn(UserSignInRequest request);

    void sendMailForgotPassword(String email);

    void verify(Long userId, String token);

    String refresh(String token);

    void setPassword(Long userId, String token, UserForgotPasswordRequest request);

    void changePassword(UserChangePasswordRequest request);
}
