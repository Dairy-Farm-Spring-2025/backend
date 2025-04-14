package com.capstone.dfms.services.impliments;

import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.models.enums.ApplicationStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.responses.ApplicationDBResponse;
import com.capstone.dfms.responses.CowTypeStatsResponse;
import com.capstone.dfms.responses.DashboardResponse;
import com.capstone.dfms.responses.ReportTaskDBResponse;
import com.capstone.dfms.services.IDashboardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DashboardService implements IDashboardService {
    private final IDailyMilkRepository dailyMilkRepository;
    private final IApplicationRepository applicationRepository;
    private final ITaskRepository taskRepository;
    private final IReportTaskRepository reportTaskRepository;
    private final IExportItemRepository exportItemRepository;
    private final ICowRepository cowRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Override
    public DashboardResponse getTodayStats() {
        LocalDate today = LocalDate.now();

        Long totalMilk = dailyMilkRepository.getTotalMilkVolumeByDate(LocalDate.now());

        List<ApplicationEntity> processingApps = applicationRepository.findByStatus(ApplicationStatus.processing);
        Long processingCount = (long) processingApps.size();

        List<ApplicationDBResponse> appDTOs = processingApps.stream().map(app -> {
            ApplicationDBResponse dto = new ApplicationDBResponse();
            dto.setApplicationId(app.getApplicationId());
            dto.setTitle(app.getTitle());
            dto.setContent(app.getContent());
            dto.setStatus(app.getStatus().name());
            dto.setFromDate(app.getFromDate());
            dto.setToDate(app.getToDate());
            dto.setRequestByName(app.getRequestBy() != null ? app.getRequestBy().getName() : null);
            dto.setTypeName(app.getType() != null ? app.getType().getName() : null);
            return dto;
        }).collect(Collectors.toList());

        List<TaskEntity> todayTasks =
                taskRepository.findTasksByDate(today);

        long totalVaccine = todayTasks.stream()
                .filter(t -> t.getVaccineInjection() != null)
                .count();

        long totalIllness = todayTasks.stream()
                .filter(t -> t.getMainIllness() != null)
                .count();

        long totalIllnessDetail = todayTasks.stream()
                .filter(t -> t.getIllness() != null)
                .count();


        long dailyTasks = todayTasks.stream()
                .filter(t -> t.getVaccineInjection() == null && t.getMainIllness() == null && t.getIllness() == null)
                .count();

        List<ReportTaskEntity> reports = reportTaskRepository.findAllByDate(today);

        List<ReportTaskDBResponse> reportDTOs = reports.stream().map(r -> {
            ReportTaskDBResponse dto = new ReportTaskDBResponse();
            dto.setReportTaskId(r.getReportTaskId());
            dto.setDescription(r.getDescription());
            dto.setStatus(r.getStatus().name());
            dto.setStartTime(r.getStartTime());
            dto.setEndTime(r.getEndTime());
            return dto;
        }).collect(Collectors.toList());

        List<Object[]> exportedItems = exportItemRepository.getExportedItemsByDate(today);
        Map<String, Float> exportedItemsMap = exportedItems.stream()
                .collect(Collectors.toMap(
                        o -> (String) o[0],
                        o -> ((Double) o[1]).floatValue()
                ));

        List<CowEntity> cows = cowRepository.findAll();

        Map<String, Map<String, Long>> grouped = cows.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCowTypeEntity() != null ? c.getCowTypeEntity().getName() : "Unknown Type",
                        Collectors.groupingBy(
                                c -> c.getCowStatus() != null ? c.getCowStatus().name() : "Unknown Status",
                                Collectors.counting()
                        )
                ));

        List<CowTypeStatsResponse> cowStats = grouped.entrySet().stream().map(entry -> {
            CowTypeStatsResponse typeStats = new CowTypeStatsResponse();
            typeStats.setCowTypeName(entry.getKey());
            typeStats.setStatusCount(entry.getValue());
            typeStats.setTotal(entry.getValue().values().stream().mapToLong(Long::longValue).sum());
            return typeStats;
        }).collect(Collectors.toList());

        Long totalWorkers = userRepository.countActiveUsersByRoleName("Worker");
        Long totalVeterinarians = userRepository.countActiveUsersByRoleName("Veterinarians");


        DashboardResponse dto = new DashboardResponse();
        dto.setTotalMilkToday(totalMilk);
        dto.setProcessingApplicationsCount(processingCount);
        dto.setProcessingApplications(appDTOs);
        dto.setTasksByVaccineInjection(totalVaccine);
        dto.setTasksByIllness(totalIllness);
        dto.setTasksByIllnessDetail(totalIllnessDetail);
        dto.setDailyTasks(dailyTasks);
        dto.setUsedItemsToday(exportedItemsMap);
        dto.setTodayReports(reportDTOs);
        dto.setTotalCow((long) cows.size());
        dto.setCowStatsByType(cowStats);
        dto.setTotalWorkers(totalWorkers);
        dto.setTotalVeterinarians(totalVeterinarians);
        return dto;
    }
}
