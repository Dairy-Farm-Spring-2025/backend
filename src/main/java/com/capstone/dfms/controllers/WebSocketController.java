package com.capstone.dfms.controllers;

import com.capstone.dfms.models.NotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;

@Controller
@Service
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendListNotificationUpdate(Long userId, List<NotificationEntity> notifications) {
        if (userId == null) {
            System.out.println("Invalid userId: " + userId);
            return;
        }
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notifications",
                notifications
        );
    }


    }
