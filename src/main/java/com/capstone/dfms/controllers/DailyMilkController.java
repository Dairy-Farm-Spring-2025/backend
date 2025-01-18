package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.mappers.IDailyMilkMapper;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.enums.MilkShift;
import com.capstone.dfms.requests.DailyMilkRequest;
import com.capstone.dfms.services.impliments.DailyMilkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/dailymilks")
@RequiredArgsConstructor
public class DailyMilkController {
    private final DailyMilkService dailyMilkService;
    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping("/create")
    public CoreApiResponse<?> createDailyMilk(
            @Valid @RequestBody DailyMilkRequest dailyMilkRequest
    ) {
        dailyMilkService.createDailyMilk(IDailyMilkMapper.INSTANCE.toModel(dailyMilkRequest));
        return CoreApiResponse.success("Create milk cow successfully!");
    }

    @GetMapping("/cow/{cowId}")
    public CoreApiResponse<List<DailyMilkEntity>> getDailyMilksByCowId(@PathVariable Long cowId) {
        List<DailyMilkEntity> dailyMilks = dailyMilkService.getDailyMilksByCowId(cowId);
        return CoreApiResponse.success(dailyMilks);
    }


    @GetMapping("/search")
    public CoreApiResponse<List<DailyMilkEntity>> searchDailyMilk(
            @RequestParam(required = false) Long cowId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) MilkShift shift
    ) {
        List<DailyMilkEntity> results = dailyMilkService.searchDailyMilk(cowId, areaId, shift);
        return CoreApiResponse.success(results);
    }

    @PutMapping("/volume/{dailyMilkId}")
    public CoreApiResponse<?> updateDailyMilkVolume(
            @PathVariable Long dailyMilkId,
            @RequestParam Long newVolume) {
        dailyMilkService.updateDailyMilkVolume(dailyMilkId, newVolume);
        return CoreApiResponse.success("Volume updated successfully.");
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteDailyMilk(@PathVariable long id) {
        dailyMilkService.deleteDailyMilk(id);
        return CoreApiResponse.success("Daily Milk deleted successfully.");
    }


}
