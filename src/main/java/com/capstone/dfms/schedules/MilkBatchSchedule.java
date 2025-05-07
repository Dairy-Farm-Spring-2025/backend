package com.capstone.dfms.schedules;

import com.capstone.dfms.models.MilkBatchEntity;
import com.capstone.dfms.models.enums.MilkBatchStatus;
import com.capstone.dfms.repositories.IMilkBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MilkBatchSchedule {
    private final IMilkBatchRepository milkBatchRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void updateExpiredMilkBatches() {
        LocalDateTime now = LocalDateTime.now();
        List<MilkBatchEntity> expiredBatches = milkBatchRepository
                .findInventoryMilkBatches(MilkBatchStatus.inventory, now);

        for (MilkBatchEntity batch : expiredBatches) {
            batch.setStatus(MilkBatchStatus.expired);
        }
        milkBatchRepository.saveAll(expiredBatches);
    }
}
