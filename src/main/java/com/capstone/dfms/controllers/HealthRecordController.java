package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.requests.HealthReportRequest;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.services.IHealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/health-record")
@RequiredArgsConstructor
public class HealthRecordController {
    private final IHealthRecordService healthRecordService;

    @PreAuthorize("hasAnyRole('VETERINARIANS')")
    @PostMapping
    public CoreApiResponse<HealthRecordEntity> createHealthReport(@RequestBody HealthReportRequest request) {
        HealthRecordEntity createdRecord = healthRecordService.createHealthReport(request);
        return CoreApiResponse.success(createdRecord);
    }

    @PreAuthorize("hasAnyRole('VETERINARIANS')")
    @PostMapping("/create-bulk")
    public CoreApiResponse<CowPenBulkResponse<HealthRecordEntity>> createBulkHealthReport(@RequestBody List<HealthReportRequest> requests) {
        return CoreApiResponse.success(healthRecordService.createBulkHealthReport(requests));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{id}")
    public CoreApiResponse<HealthRecordEntity> getHealthReportById(@PathVariable Long id) {
        HealthRecordEntity record = healthRecordService.getHealthReportById(id);
        return CoreApiResponse.success(record);
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping
    public CoreApiResponse<List<HealthRecordEntity>> getAllHealthReports() {
        List<HealthRecordEntity> records = healthRecordService.getAllHealthReports();
        return CoreApiResponse.success(records);
    }

    @PreAuthorize("hasAnyRole('MANAGER','VETERINARIANS')")
    @PutMapping("/{id}")
    public CoreApiResponse<HealthRecordEntity> updateHealthReport(@PathVariable Long id, @RequestBody HealthReportRequest request) {
        HealthRecordEntity updatedRecord = healthRecordService.updateHealthReport(id, request);
        return CoreApiResponse.success(updatedRecord);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<Void> deleteHealthReport(@PathVariable Long id) {
        healthRecordService.deleteHealthReport(id);
        return CoreApiResponse.success("Delete successfully!");
    }
}
