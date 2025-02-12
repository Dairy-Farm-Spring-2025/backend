package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MilkBatchRequest {
    private List<DailyMilkRequest> dailyMilks;
    private String shift;

}
