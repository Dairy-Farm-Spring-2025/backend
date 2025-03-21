package com.capstone.dfms.controllers;

import com.capstone.dfms.models.NotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendListNotificationUpdate(Long userId, List<NotificationEntity> notifications) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/my/notification",
                notifications
        );
    }
}
