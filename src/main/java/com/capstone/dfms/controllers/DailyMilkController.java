package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.mappers.IDailyMilkMapper;
import com.capstone.dfms.mappers.IPenMapper;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.requests.DailyMilkRequest;
import com.capstone.dfms.requests.PenCreateRequest;
import com.capstone.dfms.responses.PenResponse;
import com.capstone.dfms.services.impliments.DailyMilkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static com.capstone.dfms.mappers.IDailyMilkMapper.INSTANCE;


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
            @RequestParam(required = false) Long areaId
    ) {
        List<DailyMilkEntity> results = dailyMilkService.searchDailyMilk(cowId, areaId);
        return CoreApiResponse.success(results);
    }


}
