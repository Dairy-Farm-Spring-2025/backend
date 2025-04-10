package com.capstone.dfms.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedMealCowResponse {
    private String itemName;
    private BigDecimal quantity;
}
