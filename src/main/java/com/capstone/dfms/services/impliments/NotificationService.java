package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.models.NotificationEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.UserNotificationEntity;
import com.capstone.dfms.models.compositeKeys.UserNotificationPK;
import com.capstone.dfms.repositories.INotificationRepository;
import com.capstone.dfms.repositories.IUserNotificationRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.NotificationRequest;
import com.capstone.dfms.responses.NotificationResponse;
import com.capstone.dfms.services.INotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final IUserRepository userRepository;
    private final IUserNotificationRepository userNotificationRepository;
    private final FirebaseMessaging firebaseMessaging;


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
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            userNotification.setUser(user);
            userNotification.setNotification(savedNotification);
            userNotification.setRead(false);
            sendFirebaseNotification(user, savedNotification);
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
    public List<UserNotificationEntity> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        List<UserNotificationEntity> list = userNotificationRepository.findNotificationsByUserId(user.getId());
        return list;
    }

    private void sendFirebaseNotification(UserEntity user, NotificationEntity notification) {
        Notification fcmNotification = Notification.builder()
                .setTitle(notification.getTitle())
                .setBody(notification.getDescription())
                .build();

        Map<String, String> data = new HashMap<>();
        data.put("notificationId", notification.getNotificationId().toString());
        if (notification.getLink() != null) {
            data.put("link", notification.getLink());
        }

        // Gửi cho mobile
        if (user.getFcmTokenMobile() != null && !user.getFcmTokenMobile().isEmpty()) {
            Message mobileMessage = Message.builder()
                    .setNotification(fcmNotification)
                    .putAllData(data)
                    .setToken(user.getFcmTokenMobile())
                    .build();

            sendFcmMessage(mobileMessage, "mobile", user.getId());
        }

        // Gửi cho web
        if (user.getFcmTokenWeb() != null && !user.getFcmTokenWeb().isEmpty()) {
            Message webMessage = Message.builder()
                    .setNotification(fcmNotification)
                    .putAllData(data)
                    .setToken(user.getFcmTokenWeb())
                    .build();

            sendFcmMessage(webMessage, "web", user.getId());
        }
    }

    private void sendFcmMessage(Message message, String deviceType, Long userId) {
        try {
            String messageId = firebaseMessaging.send(message);
            System.out.println("Notification sent to " + deviceType + " for user " + userId + ": " + messageId);
        } catch (FirebaseMessagingException e) {
            System.err.println("Failed to send FCM to " + deviceType + " for user " + userId + ": " + e.getMessage());
        }
    }

}
