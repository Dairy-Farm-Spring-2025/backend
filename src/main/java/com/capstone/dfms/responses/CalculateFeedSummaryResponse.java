package com.capstone.dfms.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalculateFeedSummaryResponse {
    private int totalCow;

    private Map<String, Integer> cowTypeCount;

    private List<CalculateFoodResponse> foodList;
}
