package com.capstone.dfms.services;

import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.requests.UserForgotPasswordRequest;

public interface IUserService {
    void createAccount(UserEntity user);

    void sendMailForgotPassword(String email);

    void verify(Long userId, String token);

    String refresh(String token);

    void setPassword(Long userId, String token, UserForgotPasswordRequest request);
}
