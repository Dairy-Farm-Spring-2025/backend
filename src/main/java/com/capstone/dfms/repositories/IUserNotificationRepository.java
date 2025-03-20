package com.capstone.dfms.repositories;

import com.capstone.dfms.models.UserNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUserNotificationRepository extends JpaRepository<UserNotificationEntity, Long> {
    @Query("SELECT un FROM UserNotificationEntity un WHERE un.user.id = :userId AND un.notification.notificationId = :notificationId")
    Optional<UserNotificationEntity> findByUser_IdAndNotification_NotificationId(Long userId, Long notificationId);

    @Query("SELECT un FROM UserNotificationEntity un WHERE un.id.userId = :userId ORDER BY un.id.notificationId DESC")
    List<UserNotificationEntity> findNotificationsByUserId(@Param("userId") Long userId);

}
