package com.capstone.dfms.repositories;

import com.capstone.dfms.models.MilkBatchEntity;
import com.capstone.dfms.models.enums.MilkBatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface IMilkBatchRepository extends JpaRepository<MilkBatchEntity, Long> {
    @Query("SELECT m FROM MilkBatchEntity m WHERE m.status = :status AND m.expiryDate < :date")
    List<MilkBatchEntity> findInventoryMilkBatches(@Param("status") MilkBatchStatus status, @Param("date") LocalDateTime date);

    @Query("SELECT DISTINCT d.milkBatch FROM DailyMilkEntity d WHERE d.worker.id = :workerId AND d.milkBatch IS NOT NULL")
    List<MilkBatchEntity> findDistinctMilkBatchesByWorkerId(@Param("workerId") Long workerId);
}
