package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.MilkBatchEntity;
import com.capstone.dfms.requests.MilkBatchRequest;
import com.capstone.dfms.requests.UpdateMilkBatchRequest;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.services.IMilkBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/MilkBatch")
@RequiredArgsConstructor
public class MilkBatchController {
    private final IMilkBatchService milkBatchService;

    @PostMapping
    public CoreApiResponse<?> createMilkBatch(
            @RequestBody @RequestParam(name = "dailyMilkIds", required = false) List<Long> dailyMilkIds) {
        milkBatchService.createMilkBatch(dailyMilkIds);
        return CoreApiResponse.success("Milk batch created successfully.");
    }

    @GetMapping("/dailymilks/{milkBatchId}")
    public CoreApiResponse<List<DailyMilkEntity>> getDailyMilksInBatch(@PathVariable Long milkBatchId) {
        List<DailyMilkEntity> dailyMilks = milkBatchService.getDailyMilksInBatch(milkBatchId);
        return CoreApiResponse.success(dailyMilks);
    }

    @PutMapping("/{milkBatchId}")
    public CoreApiResponse<?> updateMilkBatch(
            @PathVariable Long milkBatchId,
            @RequestBody UpdateMilkBatchRequest request) {
        milkBatchService.updateMilkBatch(milkBatchId, request.getDailyMilkIdsToAdd(), request.getDailyMilkIdsToRemove());
        return CoreApiResponse.success("Milk Batch updated successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<MilkBatchEntity>> getAll() {
        return CoreApiResponse.success(milkBatchService.getAllMilkBatch());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<MilkBatchEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(milkBatchService.getMilkBatchById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteMilkBatch(
            @PathVariable Long id
    ){
        milkBatchService.deleteMilkBatch(id);
        return CoreApiResponse.success("Delete milk batch successfully");
    }


    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping("/create")
    public CoreApiResponse<MilkBatchEntity> createMilkBatchWithDailyMilks(@RequestBody @Valid MilkBatchRequest request) {
        MilkBatchEntity milkBatch = milkBatchService.createMilkBatchWithDailyMilks(request);
        return CoreApiResponse.success(milkBatch);
    }

}
