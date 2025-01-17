package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.MilkBatchEntity;
import com.capstone.dfms.models.enums.MilkBatchStatus;
import com.capstone.dfms.models.enums.MilkShift;
import com.capstone.dfms.repositories.IDailyMilkRepository;
import com.capstone.dfms.repositories.IMilkBatchRepository;
import com.capstone.dfms.services.IMilkBatchService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MilkBatchService implements IMilkBatchService {
    private  final IMilkBatchRepository milkBatchRepository;

    private final IDailyMilkRepository dailyMilkRepository;

    @Override
    public void createMilkBatch(List<Long> dailyMilkIds) {
        MilkBatchEntity milkBatch = new MilkBatchEntity();
        LocalDateTime now = LocalDateTime.now();
        milkBatch.setDate(now);
        milkBatch.setExpiryDate(now.plusHours(12));
        milkBatch.setStatus(MilkBatchStatus.inventory);

        List<DailyMilkEntity> dailyMilks = new ArrayList<>();

        LocalDate referenceDate = null;
        MilkShift referenceShift = null;

        for (Long dailyMilkId : dailyMilkIds) {
            DailyMilkEntity dailyMilk = dailyMilkRepository
                    .findById(dailyMilkId)
                    .orElseThrow(() -> new DataNotFoundException("Daily Milk", "id", dailyMilkId));

            if (referenceDate == null && referenceShift == null) {
                referenceDate = dailyMilk.getMilkDate();
                referenceShift = dailyMilk.getShift();
            } else {
                if (!dailyMilk.getMilkDate().equals(referenceDate)) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            "All daily milk entries must have the same milk date.");
                }
                if (!dailyMilk.getShift().equals(referenceShift)) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            "All daily milk entries must have the same milk shift.");
                }
            }

            dailyMilks.add(dailyMilk);
        }

        long totalVolume = dailyMilks.stream()
                .mapToLong(DailyMilkEntity::getVolume)
                .sum();

//        if (totalVolume > 20L) {
//            throw new AppException(HttpStatus.BAD_REQUEST,
//                    "The total volume of the milk batch must not exceed 20 liters.");
//        }

        milkBatch.setTotalVolume(totalVolume);
        MilkBatchEntity batch = milkBatchRepository.save(milkBatch);

        for (DailyMilkEntity dailyMilk : dailyMilks) {
            dailyMilk.setMilkBatch(batch);
        }
        dailyMilkRepository.saveAll(dailyMilks);
    }

    @Override
    public List<DailyMilkEntity> getDailyMilksInBatch(Long milkBatchId) {
        MilkBatchEntity milkBatch = milkBatchRepository.findById(milkBatchId)
                .orElseThrow(() -> new DataNotFoundException("Milk Batch", "id", milkBatchId));

        return dailyMilkRepository.findByMilkBatch(milkBatch);
    }

    @Override
    public List<MilkBatchEntity> getAllMilkBatch() {
        return milkBatchRepository.findAll();
    }

    @Override
    public MilkBatchEntity getMilkBatchById(Long id) {
        return milkBatchRepository.findById(id)
                .orElseThrow(()
                        -> new DataNotFoundException("Milk batch", "id", id));
    }

    @Override
    public void deleteMilkBatch(Long id) {
        MilkBatchEntity existingEntity = milkBatchRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Milk Batch", "id", id));

        List<DailyMilkEntity> relatedDailyMilks = dailyMilkRepository.findByMilkBatch(existingEntity);

        for (DailyMilkEntity dailyMilk : relatedDailyMilks) {
            dailyMilk.setMilkBatch(null);
        }
        dailyMilkRepository.saveAll(relatedDailyMilks);
        milkBatchRepository.delete(existingEntity);
    }

    @Override
    public void updateMilkBatch(Long milkBatchId, List<Long> dailyMilkIdsToAdd, List<Long> dailyMilkIdsToRemove) {
        MilkBatchEntity milkBatch = milkBatchRepository.findById(milkBatchId)
                .orElseThrow(() -> new DataNotFoundException("Milk Batch", "id", milkBatchId));

        List<DailyMilkEntity> currentDailyMilks = dailyMilkRepository.findByMilkBatch(milkBatch);

        if (dailyMilkIdsToRemove != null && !dailyMilkIdsToRemove.isEmpty()) {
            for (Long dailyMilkId : dailyMilkIdsToRemove) {
                DailyMilkEntity dailyMilk = dailyMilkRepository.findById(dailyMilkId)
                        .orElseThrow(() -> new DataNotFoundException("Daily Milk", "id", dailyMilkId));
                if (!currentDailyMilks.contains(dailyMilk)) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            "Daily Milk ID " + dailyMilkId + " is not part of this Milk Batch.");
                }
                dailyMilk.setMilkBatch(null);
                dailyMilkRepository.save(dailyMilk);
            }
        }

        if (dailyMilkIdsToAdd != null && !dailyMilkIdsToAdd.isEmpty()) {
            for (Long dailyMilkId : dailyMilkIdsToAdd) {
                DailyMilkEntity dailyMilk = dailyMilkRepository.findById(dailyMilkId)
                        .orElseThrow(() -> new DataNotFoundException("Daily Milk", "id", dailyMilkId));
                if (dailyMilk.getMilkBatch() != null) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            "Daily Milk ID " + dailyMilkId + " already belongs to another Milk Batch.");
                }
                dailyMilk.setMilkBatch(milkBatch);
                dailyMilkRepository.save(dailyMilk);
            }
        }

        List<DailyMilkEntity> updatedDailyMilks = dailyMilkRepository.findByMilkBatch(milkBatch);
        long totalVolume = updatedDailyMilks.stream().mapToLong(DailyMilkEntity::getVolume).sum();
        if (totalVolume > 20L) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "The total volume of the milk batch must not exceed 20 liters.");
        }
        milkBatch.setTotalVolume(totalVolume);
        milkBatchRepository.save(milkBatch);
    }

}
