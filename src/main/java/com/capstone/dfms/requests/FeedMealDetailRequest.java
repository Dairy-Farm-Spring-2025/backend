package com.capstone.dfms.requests;

import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.enums.FeedMealShift;
import com.capstone.dfms.models.enums.ItemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedMealDetailRequest {

    private Long quantity;

    private Long itemId;

}
