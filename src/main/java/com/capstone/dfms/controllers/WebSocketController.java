package com.capstone.dfms.controllers;

import com.capstone.dfms.models.UserNotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
                "/queue/notifications",
                notifications
        );
    }

    public void sendNotification(UserNotificationEntity notification) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getUser().getId()),
                "/notifications",
                notification
        );
    }

}
