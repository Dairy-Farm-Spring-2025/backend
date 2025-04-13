package com.capstone.dfms.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowTypeStatsResponse {
    private String cowTypeName;
    private Long total;
    private Map<String, Long> statusCount;
}
