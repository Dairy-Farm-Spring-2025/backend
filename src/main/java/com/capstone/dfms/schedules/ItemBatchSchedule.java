package com.capstone.dfms.schedules;

import com.capstone.dfms.models.ItemBatchEntity;
import com.capstone.dfms.models.enums.BatchStatus;
import com.capstone.dfms.repositories.IItemBatchRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
public class ItemBatchSchedule {
    private final IItemBatchRepository itemBatchRepository;
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateItemBatchStatus() {
        List<ItemBatchEntity> batches = itemBatchRepository.findAll();

        for (ItemBatchEntity batch : batches) {
             if (batch.getExpiryDate().isBefore(LocalDate.now()) && batch.getStatus() != BatchStatus.expired) {
                batch.setStatus(BatchStatus.expired);
            }
        }

        itemBatchRepository.saveAll(batches);
    }
}
