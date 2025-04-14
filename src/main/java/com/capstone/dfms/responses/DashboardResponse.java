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
    private Long tasksByVaccineInjection;
    private Long tasksByIllness;
    private Long tasksByIllnessDetail;
    private Map<String, Float> usedItemsToday;
    private Long dailyTasks;
    private List<ReportTaskDBResponse> todayReports;
    private Long totalCow;
    private List<CowTypeStatsResponse> cowStatsByType;
    private Long totalWorkers;
    private Long totalVeterinarians;

}
