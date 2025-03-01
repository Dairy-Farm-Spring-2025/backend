package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.IDailyMilkMapper;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.enums.MilkShift;
import com.capstone.dfms.requests.DailyMilkRequest;
import com.capstone.dfms.responses.MonthlyMilkSummaryResponse;
import com.capstone.dfms.responses.RangeDailyMilkResponse;
import com.capstone.dfms.responses.TotalMilkTodayResponse;
import com.capstone.dfms.services.impliments.DailyMilkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
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
        return CoreApiResponse.success(LocalizationUtils.getMessage("milk.create.success")
        );
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

    @GetMapping("/search_available")
    public CoreApiResponse<List<DailyMilkEntity>> searchDailyMilkAvailable (
            @RequestParam(required = false) Long cowId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) MilkShift shift
    ) {
        List<DailyMilkEntity> results = dailyMilkService.searchDailyMilkAvailable(cowId, areaId, shift);
        return CoreApiResponse.success(results);
    }

    @PutMapping("/volume/{dailyMilkId}")
    public CoreApiResponse<?> updateDailyMilkVolume(
            @PathVariable Long dailyMilkId,
            @RequestParam Long newVolume) {
        dailyMilkService.updateDailyMilkVolume(dailyMilkId, newVolume);
        return CoreApiResponse.success(LocalizationUtils.getMessage("milk.update.volume.success")
        );
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteDailyMilk(@PathVariable long id) {
        dailyMilkService.deleteDailyMilk(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("milk.delete.success")
        );
    }

    @GetMapping("/total/day")
    public CoreApiResponse<TotalMilkTodayResponse> getTotalMilk(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate milkDate) {
        TotalMilkTodayResponse response = dailyMilkService.getTotalMilkVolumeForDate(milkDate);
        return CoreApiResponse.success(response);
    }

    @GetMapping("/total/month")
    public CoreApiResponse<List<MonthlyMilkSummaryResponse>> getMonthlyMilkSummary(
            @RequestParam int year) {
        List<MonthlyMilkSummaryResponse> summary = dailyMilkService.getMonthlyMilkSummary(year);
        return CoreApiResponse.success(summary);
    }

    @GetMapping("/total/{cowId}/day")
    public CoreApiResponse<TotalMilkTodayResponse> getTotalMilkByCowAndDate(
            @PathVariable Long cowId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long totalMilk = dailyMilkService.getTotalMilkByCowAndDate(cowId, date);
        TotalMilkTodayResponse response = new TotalMilkTodayResponse(totalMilk);
        return CoreApiResponse.success(response);
    }

    @GetMapping("/total/{cowId}/month")
    public CoreApiResponse<List<MonthlyMilkSummaryResponse>> getTotalMilkByMonthAndCow(
            @RequestParam int year,
            @PathVariable Long cowId) {
        List<MonthlyMilkSummaryResponse> response = dailyMilkService.getTotalMilkByMonthAndCow(year, cowId);
        return CoreApiResponse.success(response);
    }

    @GetMapping("range/{cowId}")
    public CoreApiResponse<List<RangeDailyMilkResponse>> getDailyMilkByCowAndDateRange(
            @PathVariable Long cowId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<RangeDailyMilkResponse> response = dailyMilkService.getDailyMilkByCowAndDateRange(cowId, start, end);
        return CoreApiResponse.success(response);
    }
}
