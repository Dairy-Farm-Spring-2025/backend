package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.FeedMealShift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedMealRequest {
    private String name;

    private String description;

    private Long cowTypeId;

    private CowStatus cowStatus;

    private List<FeedMealDetailRequest> details;

//    private FeedMealShift shift;

}
