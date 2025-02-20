package com.capstone.dfms.requests;

import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.enums.FeedMealShift;
import com.capstone.dfms.models.enums.ItemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedMealDetailRequest {

    private BigDecimal quantity;

    private Long itemId;

}
