package com.capstone.dfms.requests;
import com.capstone.dfms.models.enums.CategoryNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest {
    private String title;
    private String description;
    private String link;
    private CategoryNotification category;
    private List<Long> userIds;
}
