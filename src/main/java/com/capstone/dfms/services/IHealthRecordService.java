package com.capstone.dfms.services;

import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.requests.HealthReportRequest;

import java.util.List;

public interface IHealthRecordService {
    HealthRecordEntity createHealthReport(HealthReportRequest request);
    HealthRecordEntity getHealthReportById(Long id);
    List<HealthRecordEntity> getAllHealthReports();
    HealthRecordEntity updateHealthReport(Long id, HealthReportRequest request);
    void deleteHealthReport(Long id);
}
