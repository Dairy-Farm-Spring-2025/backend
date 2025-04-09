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
public class DashboardResponse {
    private Long totalMilkToday;
    private Long processingApplicationsCount;
    private List<ApplicationDBResponse> processingApplications;
    private Map<String, Long> tasksByVaccineInjection;
    private Map<String, Long> tasksByIllness;
    private Map<String, Long> tasksByIllnessDetail;
    private Map<String, Float> usedItemsToday;
    private Long dailyTasks;
    private List<ReportTaskDBResponse> todayReports;
}
