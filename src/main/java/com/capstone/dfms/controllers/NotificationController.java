package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.NotificationEntity;
import com.capstone.dfms.models.UserNotificationEntity;
import com.capstone.dfms.requests.NotificationRequest;
import com.capstone.dfms.services.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final INotificationService notificationService;

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<NotificationEntity> createNotification(
            @RequestBody NotificationRequest notification) {
        NotificationEntity notificationEntity = notificationService.createNotification(notification);
        return CoreApiResponse.success("Create notification successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("")
    public CoreApiResponse<List<NotificationEntity>> getAllNotifications() {
        return CoreApiResponse.success(notificationService.getAllNotifications());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<NotificationEntity> getNotificationById(@PathVariable Long id) {
        return CoreApiResponse.success(notificationService.getNotificationById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return CoreApiResponse.success("Deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @PutMapping("/{notificationId}/mark-read/{userId}")
    public CoreApiResponse<?> markAsRead(@PathVariable Long notificationId, @PathVariable Long userId) {
        notificationService.markAsRead(notificationId, userId);
        return CoreApiResponse.success("Read notification success");
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/myNotification")
    public CoreApiResponse<List<UserNotificationEntity>> getUserNotifications() {
        List<UserNotificationEntity> notifications = notificationService.getUserNotifications();
        return CoreApiResponse.success(notifications);
    }

}
