package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.constants.ImageContants;
import com.capstone.dfms.components.events.MailEvent;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.TokenProvider;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.components.utils.PasswordUtils;
import com.capstone.dfms.components.utils.UploadImagesUtils;
import com.capstone.dfms.mappers.UserMapper;
import com.capstone.dfms.models.RoleEntity;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.models.TokenEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.TaskShift;
import com.capstone.dfms.models.enums.UserStatus;
import com.capstone.dfms.repositories.IRoleRepository;
import com.capstone.dfms.repositories.ITaskRepository;
import com.capstone.dfms.repositories.ITokenRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.SignInResponse;
import com.capstone.dfms.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleRepository roleRepository;
    private final ITokenRepository tokenRepository;
    private final TokenProvider tokenProvider;
    private final ITaskRepository taskRepository;

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
                throw new AppException(HttpStatus.CONFLICT, LocalizationUtils.getMessage("user.create.exist"));

        }
        String defaultPassword = PasswordUtils.generateRandomString(8);
        user.setEmailVerified(false);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setProfilePhoto(ImageContants.DEFAULT_AVATAR);
        user.setIsActive(true);
        user.setChangePassword(false);
        user.setUpdateInfo(false);
        user.setStatus(UserStatus.active);

        RoleEntity role = roleRepository.findById(user.getRoleId().getId()).orElseThrow(()
                -> new AppException(HttpStatus.NOT_FOUND,LocalizationUtils.getMessage("user.login.role_not_exist")));
        user.setRoleId(role);

        String employeeNumber = generateEmployeeNumberByRole(role.getId());
        user.setEmployeeNumber(employeeNumber);

         userRepository.save(user);

        MailEvent mailEvent = new MailEvent(defaultPassword,this, user, "information");
        applicationEventPublisher.publishEvent(mailEvent);
    }
    @Override
    public SignInResponse signIn(UserSignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

//        if(!userPrincipal.getUser().getEmailVerified()){
//            sendVerifyMail(userPrincipal.getUser());
//            throw new AppException(HttpStatus.UNAUTHORIZED, "Email not verified. A verification email has been sent to your registered email address.");
//        }
        if (!userPrincipal.getUser().getIsActive()) {
            throw new AppException(HttpStatus.UNAUTHORIZED, LocalizationUtils.getMessage("user.login.user_is_locked"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userPrincipal.getId())
                .fullName(userPrincipal.getUsername())
                .roleName(userPrincipal.getUser().getRoleId().getName())
                .build();
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
                        new AppException(HttpStatus.NOT_FOUND,"Refresh Token not found with token : " + token)
                );
        if(refreshToken.isRevoked()){
            throw new AppException(HttpStatus.UNAUTHORIZED,"Token đã bị thu hồi");
        }
        if(refreshToken.isExpired()){
            throw new AppException(HttpStatus.UNAUTHORIZED,"Token đã hết hạn");
        }
        if(refreshToken.getExpirationDate().isBefore(LocalDate.now())){
            refreshToken.setExpired(true);
            throw new AppException(HttpStatus.UNAUTHORIZED,"Token đã hết hạn");
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
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("user.update.lock"));
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

    @Override
    public void changePassword(UserChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();


        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("user.password_not_match"));
        }

        if (!request.getNewPassword().equals(request.getConfirmedPassword())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("user.password_validate"));
        }
        user.setChangePassword(true);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserEntity getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        return user;
    }

    @Override
    public UserEntity updatePersonalInformation(PersonalUpdateRequest update, MultipartFile imageFile) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        if (update.getDob() != null) {
            LocalDate dob = update.getDob();
            LocalDate now = LocalDate.now();
            int age = Period.between(dob, now).getYears();
            if (age < 18) {
                throw new AppException(HttpStatus.BAD_REQUEST,LocalizationUtils.getMessage("user.dob_validate"));
            }
        }
        UserMapper.INSTANCE.updateUserFromRequest(update, user);

        user.setUpdateInfo(true);
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setProfilePhoto(UploadImagesUtils.storeFile(imageFile, ImageContants.USERS_IMAGE_PATH));
        }
        return userRepository.save(user);
    }

    @Override
    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> getWorkers() {
        Long fixedRoleId = 4L;
        return userRepository.findByRoleId(fixedRoleId);
    }

    @Override
    public List<UserEntity> getVeterinarians() {
        Long fixedRoleId = 3L;
        return userRepository.findByRoleId(fixedRoleId);
    }

    @Override
    public List<UserEntity> getAvailableVeterinarians(LocalDate date) {
        return userRepository.findAvailableVet(date);
    }

    @Override
    public void banUser(Long id) {
        UserEntity existingUser = userRepository
                .findById(id)
                .orElseThrow(()
                        -> new DataNotFoundException("User", "id", id));
        existingUser.setIsActive(false);
        existingUser.setStatus(UserStatus.quitJob);
        userRepository.save(existingUser);
    }


    @Override
    public void unbanUser(Long id) {
        UserEntity existingUser = userRepository
                .findById(id)
                .orElseThrow(()
                        -> new DataNotFoundException("User", "id", id));
        existingUser.setIsActive(true);
        existingUser.setStatus(UserStatus.active);
        userRepository.save(existingUser);
    }

    @Override
    public void updateOnLeave(Long id) {
        UserEntity existingUser = userRepository
                .findById(id)
                .orElseThrow(()
                        -> new DataNotFoundException("User", "id", id));
        existingUser.setStatus(UserStatus.onLeave);
        userRepository.save(existingUser);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()
                        -> new DataNotFoundException("User", "id", id));
    }
    @Override
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public UserEntity changeUserRole(Long userId,Long roleId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("user.not_exist")));

        RoleEntity newRole = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("user.login.role_not_exist")));

        user.setRoleId(newRole);
        userRepository.save(user);

        return userRepository.save(user); // Convert entity to response DTO
    }

    private String generateEmployeeNumberByRole(Long roleId) {
        long count = userRepository.count();

        String employeeNumberPrefix = null;
        if (roleId == 1) {
            employeeNumberPrefix = "AD";
        }if (roleId == 2) {
            employeeNumberPrefix = "MA";
        }if (roleId == 3) {
            employeeNumberPrefix = "VE";
        } if (roleId == 4) {
            employeeNumberPrefix = "WO";
        }
        return employeeNumberPrefix + String.format("%03d", count + 1);
    }

    @Override
    public void updateFcmToken(FcmTokenRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        if (request.getFcmTokenMobile() != null) {
            user.setFcmTokenMobile(request.getFcmTokenMobile());
        }
        if (request.getFcmTokenWeb() != null) {
            user.setFcmTokenWeb(request.getFcmTokenWeb());
        }

        userRepository.save(user);
    }

    @Override
    public List<UserEntity> getAvailableUsers(AvailableUserRequest request) {
        List<UserEntity> allUsers = userRepository.findAllActiveUsersByRoleId(request.getRoleId());
        List<UserEntity> availableUsers = new ArrayList<>();

        for (UserEntity user : allUsers) {
            boolean isAvailable = true;
            LocalDate currentDate = request.getFromDate();

            while (!currentDate.isAfter(request.getToDate())) {
                List<TaskEntity> tasks = taskRepository.findByAssignee_IdAndDate(user.getId(), currentDate);

                if (request.getRoleId() == 4) {
                    boolean hasNoTask = tasks.isEmpty();
                    boolean hasSameAreaTask = tasks.stream().allMatch(t ->
                            t.getAreaId() != null && t.getAreaId().getAreaId().equals(request.getAreaId()));

                    if (!(hasNoTask || hasSameAreaTask)) {
                        isAvailable = false;
                        break;
                    }
                } else {
                    if (!tasks.isEmpty()) {
                        isAvailable = false;
                        break;
                    }
                }

                currentDate = currentDate.plusDays(1);
            }

            if (isAvailable) {
                availableUsers.add(user);
            }
        }

        return availableUsers;
    }

    @Override
    public List<UserEntity> getUserforNightShift(LocalDate fromDate, LocalDate toDate) {

        List<UserEntity> users = userRepository.findAllActiveUsersByRoleId(4L);
        List<UserEntity> availableUsers = new ArrayList<>();

        for (UserEntity user : users) {
            boolean isAvailable = true;
            LocalDate currentDate = fromDate;

            while (!currentDate.isAfter(toDate)) {
                List<TaskEntity> tasks = taskRepository.findByAssigneeShiftDate
                        (user.getId(), TaskShift.nightShift, currentDate);
                if (!tasks.isEmpty()) {
                    isAvailable = false;
                    break;
                }
                currentDate = currentDate.plusDays(1);
            }
            if (isAvailable) {
                availableUsers.add(user);
            }
        }
        return availableUsers;
    }

}
