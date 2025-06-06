package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.configurations.AppProperties;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.RefreshResponse;
import com.capstone.dfms.responses.SignInResponse;
import com.capstone.dfms.services.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.capstone.dfms.mappers.UserMapper.INSTANCE;
@RestController
@RequestMapping("${app.api.version.v1}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    private final LocalizationUtils localizationUtils;


    @Autowired
    private AppProperties appProperties;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createAccountManager(
            @Valid @RequestBody CreateAccountRequest accountRequest
    ){
         userService.createAccount(INSTANCE.toModel(accountRequest));
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.create.successfully"));
    }


    @PostMapping("/signin")
    public CoreApiResponse<SignInResponse> signin(@Valid @RequestBody UserSignInRequest request, HttpServletResponse response) {
        SignInResponse signIn = userService.signIn(request);
        Cookie cookie = new Cookie("refreshToken", signIn.getRefreshToken());

        cookie.setMaxAge(appProperties.getAuth().getRefreshTokenExpirationMsec());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return CoreApiResponse.success(signIn, LocalizationUtils.getMessage("user.login.login_successfully"));
    }

    @GetMapping("/verify")
    public CoreApiResponse<?> verify(
            @RequestParam Long userId,
            @RequestParam String token
    ) {
        userService.verify(userId,token);
        return CoreApiResponse.success("User verified successfully");
    }

    @PostMapping("/refresh")
    public CoreApiResponse<?> refresh(
            @CookieValue(value = "refreshToken", required = false) String cookieRT,
            @RequestBody UserRefreshRequest bodyRT
    ) {
        if(bodyRT == null && !isValidToken(cookieRT)){
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid token");
        }
        String token = bodyRT != null ? bodyRT.getRefreshToken() : cookieRT;

        String accessToken = userService.refresh(token);

        return CoreApiResponse.success(new RefreshResponse(accessToken),"User refresh token successfully");
    }

    @GetMapping("/forgotpassword")
    public CoreApiResponse<?> forgotPassword(
            @Valid @RequestParam String email
    ){
        userService.sendMailForgotPassword(email);
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.forgot_password"));
    }

    @PutMapping("/setpassword")
    public CoreApiResponse<?> setPassword(
            @RequestParam("userId") Long userId,
            @RequestParam("token") String token,
            @RequestBody UserForgotPasswordRequest request
    ){
        userService.setPassword(userId, token, request);
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.password.change"));
    }
    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("/changepassword")
    public CoreApiResponse<?> changePassword(
            @RequestBody UserChangePasswordRequest request
    ){
        userService.changePassword(request);
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.password.change"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/profile")
    public CoreApiResponse<?> getCurrentUserProfile() {
        UserEntity currentUser = userService.getMyProfile();
        return CoreApiResponse.success(currentUser);
    }
    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("/update")
    public CoreApiResponse<UserEntity> updateUser(
            @Valid @ModelAttribute PersonalUpdateRequest updateUserRequest,
            @RequestParam(name = "imageAvatar", required = false) MultipartFile imageAvatar) throws IOException {
        return CoreApiResponse.success(userService.updatePersonalInformation(updateUserRequest,imageAvatar),LocalizationUtils.getMessage("user.update.info"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/all")
    public CoreApiResponse<List<UserEntity>> getAllUsers() {
        return CoreApiResponse.success(userService.getAllUser());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<UserEntity> getUserById(@PathVariable Long id) {
        return CoreApiResponse.success(userService.getUserById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/veterinarians")
    public CoreApiResponse<List<UserEntity>> getVeterinarians() {
        List<UserEntity> users = userService.getVeterinarians();
        return CoreApiResponse.success(users);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("available/veterinarians")
    public CoreApiResponse<List<UserEntity>>
    getAvailableVeterinarians(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<UserEntity> users = userService.getAvailableVeterinarians(date);
        return CoreApiResponse.success(users);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/workers")
    public CoreApiResponse<List<UserEntity>> getworkers() {
        List<UserEntity> users = userService.getWorkers();
        return CoreApiResponse.success(users);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/ban/{id}")
    public CoreApiResponse<?> banUser(@PathVariable Long id) {
        userService.banUser(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.update.lock"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/unban/{id}")
    public CoreApiResponse<?> unbanUser(@PathVariable Long id) {
        userService.unbanUser(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.update.unlock"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/onleave/{id}")
    public CoreApiResponse<?> updateUser(@PathVariable Long id) {
        userService.updateOnLeave(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.update.status"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/roles")
    public CoreApiResponse<List<RoleEntity>> getAllRoles() {
        return CoreApiResponse.success(userService.getAllRoles());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/changerole/{userId}/{roleId}")
    public CoreApiResponse<?> changeUserRole(@PathVariable Long userId, @PathVariable Long roleId ) {
         userService.changeUserRole(userId,roleId);
        return CoreApiResponse.success(LocalizationUtils.getMessage("user.update.role"));
    }

    private boolean isValidToken(String token) {
        return token != null && isJWT(token);
    }
    private boolean isJWT(String token) {
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("/update/fcmToken")
    public CoreApiResponse<String> updateFcmToken(
            @RequestBody FcmTokenRequest request) {
        userService.updateFcmToken(request);
        return CoreApiResponse.success("FCM token updated successfully");
    }

    @GetMapping("/free")
    public CoreApiResponse<List<UserEntity>> getAvailableUsers(
            @RequestParam Long roleId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) Long areaId) {

        AvailableUserRequest request = new AvailableUserRequest();
        request.setRoleId(roleId);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setAreaId(areaId);

        List<UserEntity> users = userService.getAvailableUsers(request);
        return CoreApiResponse.success(users);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("import/free")
    public CoreApiResponse<List<UserEntity>> getAvailableUsersImport(
            @RequestParam Long roleId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) String areaName) {

        AvailableUserImportRequest request = new AvailableUserImportRequest();
        request.setRoleId(roleId);
        request.setFromDate(fromDate);
        request.setToDate(toDate);
        request.setAreaName(areaName);

        List<UserEntity> users = userService.getAvailableUsersImport(request);
        return CoreApiResponse.success(users);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/free/night")
    public CoreApiResponse<List<UserEntity>> getFreeNightShiftUsers(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate){
        List<UserEntity> users = userService.getUserforNightShift(fromDate,toDate);
        return CoreApiResponse.success(users);
    }
}
