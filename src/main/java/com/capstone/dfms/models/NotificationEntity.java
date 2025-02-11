package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.CategoryNotification;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String description;

    private String link;

    private CategoryNotification category;

}
