package com.capstone.dfms.services;

import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.requests.HealthReportRequest;
import com.capstone.dfms.responses.CowHealthInfoResponse;
import com.capstone.dfms.responses.CowPenBulkResponse;

import java.util.List;

public interface IHealthRecordService {
    HealthRecordEntity createHealthReport(HealthReportRequest request);
    HealthRecordEntity getHealthReportById(Long id);
    List<HealthRecordEntity> getAllHealthReports();
    HealthRecordEntity updateHealthReport(Long id, HealthReportRequest request);
    void deleteHealthReport(Long id);
    CowPenBulkResponse<HealthRecordEntity> createBulkHealthReport(List<HealthReportRequest> requests);
}
