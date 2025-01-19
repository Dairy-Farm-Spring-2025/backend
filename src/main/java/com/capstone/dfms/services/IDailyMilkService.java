package com.capstone.dfms.services;

import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.enums.MilkShift;
import com.capstone.dfms.responses.TotalMilkTodayResponse;

import java.time.LocalDate;
import java.util.List;

public interface IDailyMilkService {
    void createDailyMilk(DailyMilkEntity dailyMilk);

    List<DailyMilkEntity> getDailyMilksByCowId(Long cowId);

    List<DailyMilkEntity> searchDailyMilk(Long cowId, Long areaId, MilkShift shift);

    void updateDailyMilkVolume(Long dailyMilkId, Long newVolume);

    void deleteDailyMilk(long id);

    TotalMilkTodayResponse getTotalMilkVolumeForDate(LocalDate milkDate);
}
