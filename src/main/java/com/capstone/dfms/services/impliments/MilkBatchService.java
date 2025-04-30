package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.MilkBatchEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.DailyMilkStatus;
import com.capstone.dfms.models.enums.MilkBatchStatus;
import com.capstone.dfms.models.enums.MilkShift;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IDailyMilkRepository;
import com.capstone.dfms.repositories.IMilkBatchRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.DailyMilkRequest;
import com.capstone.dfms.requests.MilkBatchRequest;
import com.capstone.dfms.services.IMilkBatchService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MilkBatchService implements IMilkBatchService {
    private final IMilkBatchRepository milkBatchRepository;

    private final IDailyMilkRepository dailyMilkRepository;

    private final IUserRepository userRepository;

    private final ICowRepository cowRepository;

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
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                            LocalizationUtils.getMessage("milk.batch.daily.milk.not.found", dailyMilkId)));

            if (referenceDate == null && referenceShift == null) {
                referenceDate = dailyMilk.getMilkDate();
                referenceShift = dailyMilk.getShift();
            } else {
                if (!dailyMilk.getMilkDate().equals(referenceDate)) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            LocalizationUtils.getMessage("milk.batch.daily.milk.same.date")
                    );
                }
                if (!dailyMilk.getShift().equals(referenceShift)) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            LocalizationUtils.getMessage("milk.batch.daily.milk.same.shift"));
                }
            }

            dailyMilks.add(dailyMilk);
        }

        long totalVolume = dailyMilks.stream()
                .mapToLong(DailyMilkEntity::getVolume)
                .sum();

        milkBatch.setTotalVolume(totalVolume);
        MilkBatchEntity batch = milkBatchRepository.save(milkBatch);

        for (DailyMilkEntity dailyMilk : dailyMilks) {
            dailyMilk.setMilkBatch(batch);
            dailyMilk.setStatus(DailyMilkStatus.inMilkBatch);
        }
        dailyMilkRepository.saveAll(dailyMilks);
    }

    @Override
    public List<DailyMilkEntity> getDailyMilksInBatch(Long milkBatchId) {
        MilkBatchEntity milkBatch = milkBatchRepository.findById(milkBatchId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("milk.batch.not.found", milkBatchId)));
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
                        -> new AppException(HttpStatus.BAD_REQUEST,LocalizationUtils.getMessage("milk.batch.not.found", id)));
    }

    @Override
    public void deleteMilkBatch(Long id) {
        MilkBatchEntity existingEntity = milkBatchRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,LocalizationUtils.getMessage("milk.batch.not.found", id)));

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
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,LocalizationUtils.getMessage("milk.batch.not.found", milkBatchId)));

        List<DailyMilkEntity> currentDailyMilks = dailyMilkRepository.findByMilkBatch(milkBatch);

        if (dailyMilkIdsToRemove != null && !dailyMilkIdsToRemove.isEmpty()) {
            for (Long dailyMilkId : dailyMilkIdsToRemove) {
                DailyMilkEntity dailyMilk = dailyMilkRepository.findById(dailyMilkId)
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,LocalizationUtils.getMessage("milk.batch.not.found", dailyMilkId)));
                if (!currentDailyMilks.contains(dailyMilk)) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            LocalizationUtils.getMessage("milk.batch.daily.milk.not.in.batch"));
                }
                dailyMilk.setMilkBatch(null);
                dailyMilk.setStatus(DailyMilkStatus.pending);
                dailyMilkRepository.save(dailyMilk);
            }
        }

        if (dailyMilkIdsToAdd != null && !dailyMilkIdsToAdd.isEmpty()) {
            for (Long dailyMilkId : dailyMilkIdsToAdd) {
                DailyMilkEntity dailyMilk = dailyMilkRepository.findById(dailyMilkId)
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                                LocalizationUtils.getMessage("milk.batch.daily.milk.not.found", dailyMilkId)));
                if (dailyMilk.getMilkBatch() != null) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            LocalizationUtils.getMessage("milk.batch.daily.milk.already.in.batch"));
                }
                dailyMilk.setMilkBatch(milkBatch);
                dailyMilk.setStatus(DailyMilkStatus.inMilkBatch);
                dailyMilkRepository.save(dailyMilk);
            }
        }

        List<DailyMilkEntity> updatedDailyMilks = dailyMilkRepository.findByMilkBatch(milkBatch);
        long totalVolume = updatedDailyMilks.stream().mapToLong(DailyMilkEntity::getVolume).sum();

        milkBatch.setTotalVolume(totalVolume);
        milkBatchRepository.save(milkBatch);
    }

    @Override
    public MilkBatchEntity createMilkBatchWithDailyMilks(MilkBatchRequest request) {

        Set<Long> cowIdSet = new HashSet<>();
        for (DailyMilkRequest dailyMilkRequest : request.getDailyMilks()) {
            if (!cowIdSet.add(dailyMilkRequest.getCowId())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("milk.batch.daily.milk.duplicate.cow"));
            }
        }
        MilkBatchEntity milkBatch = MilkBatchEntity.builder()
                .totalVolume(0L)
                .date(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(1))
                .status(MilkBatchStatus.inventory)
                .dailyMilks(new ArrayList<>())
                .build();


        long totalVolume = 0;

        for (DailyMilkRequest dailyMilkRequest : request.getDailyMilks()) {
            CowEntity cow = cowRepository.findById(dailyMilkRequest.getCowId())
                    .orElseThrow(() -> new DataNotFoundException("Cow", "id", dailyMilkRequest.getCowId()));

            long milkCountToday = dailyMilkRepository.countByCowAndMilkDate(cow, LocalDate.now());
            if (milkCountToday >= 2) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("milk.create.error.limit"));
            }

            if (cow.getCowStatus() != CowStatus.milkingCow) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("milk.create.error.status"));
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            UserEntity user = userPrincipal.getUser();

            DailyMilkEntity dailyMilk = DailyMilkEntity.builder()
                    .shift(MilkShift.valueOf(request.getShift()))
                    .milkDate(LocalDate.now())
                    .volume(dailyMilkRequest.getVolume())
                    .status(DailyMilkStatus.pending)
                    .worker(user)
                    .cow(cow)
                    .milkBatch(milkBatch)
                    .build();

            milkBatch = milkBatchRepository.save(milkBatch);
            totalVolume += dailyMilk.getVolume();
            milkBatch.getDailyMilks().add(dailyMilk);
            dailyMilkRepository.save(dailyMilk);
        }


        milkBatch.setTotalVolume(totalVolume);
        return milkBatchRepository.save(milkBatch);
    }

    @Override
    public void markMilkBatchesAsOutOfStock(List<Long> milkBatchIds) {
        List<MilkBatchEntity> batches = milkBatchRepository.findAllById(milkBatchIds);

        for (MilkBatchEntity batch : batches) {
            batch.setStatus(MilkBatchStatus.out_of_stock);
        }
        milkBatchRepository.saveAll(batches);
    }

    @Override
    public List<MilkBatchEntity> getMilkBatchesByWorkerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        return milkBatchRepository.findDistinctMilkBatchesByWorkerId(user.getId());
    }

}
