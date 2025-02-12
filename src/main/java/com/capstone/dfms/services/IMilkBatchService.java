package com.capstone.dfms.services;

import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.MilkBatchEntity;
import com.capstone.dfms.requests.MilkBatchRequest;

import java.util.List;

public interface IMilkBatchService {
    void createMilkBatch( List<Long> dailyMilkIds);

    List<DailyMilkEntity> getDailyMilksInBatch(Long milkBatchId);

    List<MilkBatchEntity> getAllMilkBatch();

    MilkBatchEntity getMilkBatchById(Long id);

    void deleteMilkBatch(Long id);

    void updateMilkBatch(Long milkBatchId, List<Long> dailyMilkIdsToAdd, List<Long> dailyMilkIdsToRemove);

    MilkBatchEntity createMilkBatchWithDailyMilks(MilkBatchRequest request);
}
