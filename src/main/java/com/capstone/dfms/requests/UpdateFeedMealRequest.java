package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.FeedMealStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateFeedMealRequest {
    private String name;
    private String description;
    private FeedMealStatus status;
}
