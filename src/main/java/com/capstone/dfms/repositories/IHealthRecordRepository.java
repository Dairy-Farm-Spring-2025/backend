package com.capstone.dfms.repositories;

import com.capstone.dfms.models.HealthRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHealthRecordRepository extends JpaRepository<HealthRecordEntity, Long> {
}
