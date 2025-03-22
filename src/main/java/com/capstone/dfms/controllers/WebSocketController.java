package com.capstone.dfms.controllers;

import com.capstone.dfms.models.NotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendListNotificationUpdate(Long userId, List<NotificationEntity> notifications) {

        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notifications",
                notifications
        );
    }
}
