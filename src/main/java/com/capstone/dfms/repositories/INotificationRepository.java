package com.capstone.dfms.repositories;

import com.capstone.dfms.models.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface INotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
