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
import com.capstone.dfms.models.enums.MilkShift;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IDailyMilkRepository;
import com.capstone.dfms.repositories.IMilkBatchRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.responses.MonthlyMilkSummaryResponse;
import com.capstone.dfms.responses.RangeDailyMilkResponse;
import com.capstone.dfms.responses.TotalMilkTodayResponse;
import com.capstone.dfms.services.IDailyMilkService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DailyMilkService implements IDailyMilkService {
    private final IDailyMilkRepository dailyMilkRepository;

    private final ICowRepository cowRepository;

    private final IUserRepository userRepository;

    private final IMilkBatchRepository milkBatchRepository;

    @Override
    public void createDailyMilk(DailyMilkEntity dailyMilk) {
        CowEntity cow = cowRepository.findById(dailyMilk.getCow().getCowId()).orElseThrow(()
                -> new AppException(HttpStatus.OK,"Cow not found"));

        long milkCountToday = dailyMilkRepository.countByCowAndMilkDate(cow, LocalDate.now());
        if (milkCountToday >= 2) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("milk.create.error.limit"));
        }

        if (cow.getCowStatus() != CowStatus.milkingCow) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("milk.create.error.status"));
        }
        dailyMilk.setCow(cow);
        dailyMilk.setMilkDate(LocalDate.now());
        dailyMilk.setStatus(DailyMilkStatus.pending);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        dailyMilk.setWorker(user);

        dailyMilkRepository.save(dailyMilk);
    }
    @Override
    public List<DailyMilkEntity> getDailyMilksByCowId(Long cowId) {
        return dailyMilkRepository.findByCowId(cowId);
    }

    @Override
    public List<DailyMilkEntity> searchDailyMilk(Long cowId, Long areaId, MilkShift shift) {
//        LocalDate today = LocalDate.now();
        return dailyMilkRepository.searchDailyMilk(cowId, areaId, shift);
    }

    @Override
    public List<DailyMilkEntity> searchDailyMilkAvailable(Long cowId, Long areaId, MilkShift shift) {
//        LocalDate today = LocalDate.now();
        return dailyMilkRepository.searchDailyMilkAvaible(cowId, areaId, shift);
    }

    @Override
    public void updateDailyMilkVolume(Long dailyMilkId, Long newVolume) {
        DailyMilkEntity dailyMilk = dailyMilkRepository.findById(dailyMilkId)
                .orElseThrow(() -> new DataNotFoundException("Daily Milk", "id", dailyMilkId));

        MilkBatchEntity milkBatch = dailyMilk.getMilkBatch();

        if (milkBatch != null) {
            long currentTotalVolume = milkBatch.getTotalVolume();
            long updatedTotalVolume = currentTotalVolume - dailyMilk.getVolume() + newVolume;

            milkBatch.setTotalVolume(updatedTotalVolume);
            milkBatchRepository.save(milkBatch);
        }

        dailyMilk.setVolume(newVolume);
        dailyMilkRepository.save(dailyMilk);
    }

    @Override
    public void deleteDailyMilk(long id) {
        DailyMilkEntity dailyMilk = dailyMilkRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Daily Milk", "id", id));

        if (dailyMilk.getMilkBatch() != null) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("milk.delete.error.associated")
            );
        }

        dailyMilkRepository.delete(dailyMilk);
    }

    @Override
    public TotalMilkTodayResponse getTotalMilkVolumeForDate(LocalDate milkDate) {
        Long totalMilk = dailyMilkRepository.getTotalMilkVolumeByDate(milkDate);
        return TotalMilkTodayResponse.builder()
                .totalMilk(totalMilk)
                .build();
    }

    @Override
    public List<MonthlyMilkSummaryResponse> getMonthlyMilkSummary(int year) {
        List<Object[]> results = dailyMilkRepository.getTotalMilkByMonth(year);
        return results.stream()
                .map(result -> new MonthlyMilkSummaryResponse((Integer) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

    @Override
    public Long getTotalMilkByCowAndDate(Long cowId, LocalDate date) {
        return dailyMilkRepository.getTotalMilkByCowAndDate(cowId, date);
    }
    @Override
    public List<MonthlyMilkSummaryResponse> getTotalMilkByMonthAndCow(int year, Long cowId) {
        List<Object[]> result = dailyMilkRepository.getTotalMilkByMonthAndCow(year, cowId);
        return result.stream()
                .map(obj -> new MonthlyMilkSummaryResponse(
                        (Integer) obj[1],
                        (Long) obj[2]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RangeDailyMilkResponse> getDailyMilkByCowAndDateRange(Long cowId, LocalDate startDate, LocalDate endDate) {
        List<DailyMilkEntity> dailyMilks = dailyMilkRepository.findByCowIdAndMilkDateBetween(cowId, startDate, endDate);

        Map<LocalDate, Long> groupedData = dailyMilks.stream()
                .collect(Collectors.groupingBy(
                        DailyMilkEntity::getMilkDate,
                        Collectors.summingLong(DailyMilkEntity::getVolume)
                ));

        List<RangeDailyMilkResponse> result = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            long volume = groupedData.getOrDefault(date, 0L);
            result.add(RangeDailyMilkResponse.builder()
                    .cowId(cowId)
                    .milkDate(date)
                    .volume(volume)
                    .build());
        }
        return result;
    }

}
