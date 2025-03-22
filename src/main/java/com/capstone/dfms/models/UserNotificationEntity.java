package com.capstone.dfms.models;

import com.capstone.dfms.models.compositeKeys.UserNotificationPK;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_notifications")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserNotificationEntity {
    @EmbeddedId
    private UserNotificationPK id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("notificationId")
    @JoinColumn(name = "notification_id")
    private NotificationEntity notification;

    private boolean isRead;
}
