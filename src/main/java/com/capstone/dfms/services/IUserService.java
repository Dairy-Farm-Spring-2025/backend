package com.capstone.dfms.services;

import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.SignInResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    void createAccount(UserEntity user);

    SignInResponse signIn(UserSignInRequest request);

    void sendMailForgotPassword(String email);

    void verify(Long userId, String token);

    String refresh(String token);

    void setPassword(Long userId, String token, UserForgotPasswordRequest request);

    void changePassword(UserChangePasswordRequest request);

    UserEntity  getMyProfile();

    UserEntity updatePersonalInformation(PersonalUpdateRequest update, MultipartFile imageFile) throws IOException;

    List<UserEntity> getAllUser();

    List<UserEntity> getWorkers();

    List<UserEntity> getVeterinarians();

    void banUser(Long id);

    void unbanUser(Long id);

    void updateOnLeave(Long id);

    List<RoleEntity> getAllRoles();

    UserEntity getUserById(Long id);

    UserEntity changeUserRole(Long userId,Long roleId);

    void updateFcmToken(FcmTokenRequest request);
}
