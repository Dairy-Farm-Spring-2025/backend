package com.capstone.dfms.controllers;

import com.capstone.dfms.models.NotificationEntity;
import com.capstone.dfms.models.UserNotificationEntity;
import com.capstone.dfms.requests.NotificationRequest;
import com.capstone.dfms.responses.NotificationResponse;
import com.capstone.dfms.services.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public void sendListNotificationUpdate(Long userId, List<UserNotificationEntity> notifications) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/notifications",
                notifications
        );
    }

//    @MessageMapping("/user/{userId}/notifications")
//    public void sendNotifications(@DestinationVariable Long userId) {
//        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(userId);
//        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notifications);
//    }

}
