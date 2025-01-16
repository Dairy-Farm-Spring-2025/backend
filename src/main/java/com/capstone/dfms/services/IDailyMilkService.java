package com.capstone.dfms.services;

import com.capstone.dfms.models.DailyMilkEntity;

import java.util.List;

public interface IDailyMilkService {
    DailyMilkEntity createDailyMilk(DailyMilkEntity dailyMilk);

    List<DailyMilkEntity> getDailyMilksByCowId(Long cowId);

    List<DailyMilkEntity> searchDailyMilk(Long cowId, Long areaId);
}
