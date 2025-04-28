package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.FeedMealDetailEntity;
import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.requests.DryMatterRequest;
import com.capstone.dfms.requests.FeedMealDetailRequest;
import com.capstone.dfms.requests.FeedMealRequest;
import com.capstone.dfms.requests.UpdateFeedMealRequest;
import com.capstone.dfms.responses.DryMatterResponse;
import com.capstone.dfms.services.IFeedMealService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/feedmeals")
@RequiredArgsConstructor
public class FeedMealController {
    private final IFeedMealService feedMealService;

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIANS','MANAGER')")
    @PostMapping
    public CoreApiResponse<?> createFeedMeal(@RequestBody FeedMealRequest request) {
        FeedMealEntity feedMealEntity = feedMealService.createFeedMeal(request);
        return CoreApiResponse.success(feedMealEntity, LocalizationUtils.getMessage("general.create_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<FeedMealEntity> getFeedMealById(@PathVariable Long id) {
        FeedMealEntity feedMealEntity = feedMealService.getFeedMealById(id);
        return CoreApiResponse.success(feedMealEntity);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<FeedMealEntity>> getAllFeedMeals() {

        return CoreApiResponse.success(feedMealService.getAllFeedMeals());
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIANS','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteFeedMeal(
            @PathVariable Long id
    ){
        feedMealService.deleteFeedMeal(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.delete_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIANS','MANAGER')")
    @PostMapping("drymatter")
    public CoreApiResponse<DryMatterResponse> calculateDryMatter(@RequestBody DryMatterRequest request) {
        double dryMatter = feedMealService.calculateDryMatter(request.getCowStatus(), request.getCowTypeId());
        DryMatterResponse response = new DryMatterResponse(dryMatter);
        return CoreApiResponse.success(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/calculate/{areaId}")
    public CoreApiResponse<?> getFeedForArea(@PathVariable Long areaId) {
        return CoreApiResponse.success(feedMealService.calculateFeedForArea(areaId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIANS','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<FeedMealEntity> updateFeedMeal(
            @PathVariable Long id,
            @RequestBody UpdateFeedMealRequest request) {
        FeedMealEntity updatedFeedMeal = feedMealService.updateFeedMeal(id, request);
        return CoreApiResponse.success(updatedFeedMeal);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIANS','MANAGER')")
    @PutMapping("detail/{id}")
    public CoreApiResponse<FeedMealDetailEntity> updateFeedMealDetail(
            @PathVariable Long id,
            @RequestParam BigDecimal quantity) {
        FeedMealDetailEntity updatedDetail = feedMealService.updateFeedMealDetail(id, quantity);
        return CoreApiResponse.success(updatedDetail);
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIANS','MANAGER')")
    @PostMapping("addDetail/{feedMealId}")
    public CoreApiResponse<String> addFeedMealDetail(
            @PathVariable Long feedMealId,
            @RequestBody FeedMealDetailRequest request) {
        feedMealService.addFeedMealDetail(feedMealId, request);
        return CoreApiResponse.success("Thêm nguyên liệu vào khẩu phần ăn thành công");
    }

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIANS','MANAGER')")
    @DeleteMapping("detail/{feedMealDetailId}")
    public CoreApiResponse<String> removeFeedMealDetail(@PathVariable Long feedMealDetailId) {
        feedMealService.removeFeedMealDetail(feedMealDetailId);
        return CoreApiResponse.success("Xóa nguyên liệu khỏi khẩu phần ăn thành công");
    }


}
