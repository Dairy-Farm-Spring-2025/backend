package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.HealthRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IHealthRecordRepository extends JpaRepository<HealthRecordEntity, Long> {
    // In HealthRecordRepository
    boolean existsByCowEntity_CowIdAndReportTimeBetween(Long cowId, LocalDateTime start, LocalDateTime end);
    List<HealthRecordEntity> findByCowEntityCowId(Long cowId);

}
