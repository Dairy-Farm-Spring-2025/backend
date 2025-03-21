package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.controllers.WebSocketController;
import com.capstone.dfms.models.NotificationEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.UserNotificationEntity;
import com.capstone.dfms.models.compositeKeys.UserNotificationPK;
import com.capstone.dfms.repositories.INotificationRepository;
import com.capstone.dfms.repositories.IUserNotificationRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.NotificationRequest;
import com.capstone.dfms.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final IUserRepository userRepository;
    private final IUserNotificationRepository userNotificationRepository;
    private final WebSocketController webSocketController;


    @Override
    public NotificationEntity createNotification(NotificationRequest request) {
        NotificationEntity notification = new NotificationEntity();
        notification.setTitle(request.getTitle());
        notification.setDescription(request.getDescription());
        notification.setLink(request.getLink());
        notification.setCategory(request.getCategory());

        NotificationEntity savedNotification = notificationRepository.save(notification);

        List<UserNotificationEntity> userNotifications = request.getUserIds().stream().map(userId -> {
            UserNotificationEntity userNotification = new UserNotificationEntity();
            userNotification.setId(new UserNotificationPK(userId, savedNotification.getNotificationId()));
            userNotification.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found")));
            userNotification.setNotification(savedNotification);
            userNotification.setRead(false);
            return userNotification;
        }).collect(Collectors.toList());

        userNotificationRepository.saveAll(userNotifications);

        return savedNotification;
    }


    @Override
    public List<NotificationEntity> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public NotificationEntity getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
    }

    @Override
    public void deleteNotification(Long id) {
        NotificationEntity notification = getNotificationById(id);
        notificationRepository.delete(notification);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        UserNotificationEntity userNotification = userNotificationRepository
                .findByUser_IdAndNotification_NotificationId(userId, notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found for user"));

        userNotification.setRead(true);
        userNotificationRepository.save(userNotification);
    }

    @Override
    public List<NotificationEntity> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        List<UserNotificationEntity> userNotifications = userNotificationRepository.findNotificationsByUserId(user.getId());

        List<NotificationEntity> list = userNotifications.stream()
                .map(UserNotificationEntity::getNotification)
                .distinct()
                .toList();
        webSocketController.sendListNotificationUpdate(user.getId(),list);
        return list;
    }
}
