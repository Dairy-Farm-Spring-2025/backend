package com.capstone.dfms.responses;

import com.capstone.dfms.models.enums.CowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowWithFeedMealResponse {

    private Long cowId;
    private String name;
    private CowStatus cowStatus;
    private String cowType;

    private Long penId;
    private String penName;

    private List<FeedMealCowResponse> feedMeals;
}
