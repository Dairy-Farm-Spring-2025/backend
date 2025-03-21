package com.capstone.dfms.services;

import com.capstone.dfms.models.NotificationEntity;
import com.capstone.dfms.requests.NotificationRequest;

import java.util.List;

public interface INotificationService {
    NotificationEntity createNotification(NotificationRequest notification);

    List<NotificationEntity> getAllNotifications();

    NotificationEntity getNotificationById(Long id);

    void deleteNotification(Long id);

    void markAsRead(Long notificationId, Long userId);

    List<NotificationEntity> getUserNotifications(Long userId);
}
