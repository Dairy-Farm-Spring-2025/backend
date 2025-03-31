package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.requests.DryMatterRequest;
import com.capstone.dfms.requests.FeedMealRequest;
import com.capstone.dfms.requests.UpdateFeedMealRequest;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.responses.DryMatterResponse;
import com.capstone.dfms.services.IFeedMealService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${app.api.version.v1}/feedmeals")
@RequiredArgsConstructor
public class FeedMealController {
    private final IFeedMealService feedMealService;

    @PostMapping
    public CoreApiResponse<?> createFeedMeal(@RequestBody FeedMealRequest request) {
        FeedMealEntity feedMealEntity = feedMealService.createFeedMeal(request);
        return CoreApiResponse.success(feedMealEntity,"Create feed meal successfully");
    }

    @GetMapping("/{id}")
    public CoreApiResponse<FeedMealEntity> getFeedMealById(@PathVariable Long id) {
        FeedMealEntity feedMealEntity = feedMealService.getFeedMealById(id);
        return CoreApiResponse.success(feedMealEntity);
    }

    @GetMapping
    public CoreApiResponse<List<FeedMealEntity>> getAllFeedMeals() {

        return CoreApiResponse.success(feedMealService.getAllFeedMeals());
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteFeedMeal(
            @PathVariable Long id
    ){
        feedMealService.deleteFeedMeal(id);
        return CoreApiResponse.success("Delete feed meal successfully");
    }

    @PostMapping("drymatter")
    public CoreApiResponse<DryMatterResponse> calculateDryMatter(@RequestBody DryMatterRequest request) {
        double dryMatter = feedMealService.calculateDryMatter(request.getCowStatus(), request.getCowTypeId());
        DryMatterResponse response = new DryMatterResponse(dryMatter);
        return CoreApiResponse.success(response);
    }

    @GetMapping("/calculate/{areaId}")
    public CoreApiResponse<?> getFeedForArea(@PathVariable Long areaId) {
        return CoreApiResponse.success(feedMealService.calculateFeedForArea(areaId));
    }

    @PutMapping("/{id}")
    public CoreApiResponse<FeedMealEntity> updateFeedMeal(
            @PathVariable Long id,
            @RequestBody UpdateFeedMealRequest request) {
        FeedMealEntity updatedFeedMeal = feedMealService.updateFeedMeal(id, request);
        return CoreApiResponse.success(updatedFeedMeal);
    }
}
