package com.capstone.dfms.services;

import com.capstone.dfms.models.FeedMealDetailEntity;
import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.requests.FeedMealDetailRequest;
import com.capstone.dfms.requests.FeedMealRequest;
import com.capstone.dfms.requests.UpdateFeedMealRequest;
import com.capstone.dfms.responses.CalculateFoodResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IFeedMealService {

    FeedMealEntity createFeedMeal(FeedMealRequest request);

    FeedMealEntity getFeedMealById(long id);

    List<FeedMealEntity> getAllFeedMeals();

    void deleteFeedMeal(long id);

    double calculateDryMatter(CowStatus cowStatus, Long cowTypeId);

    FeedMealEntity updateFeedMeal(Long feedMealId, UpdateFeedMealRequest request);

    FeedMealDetailEntity updateFeedMealDetail(Long feedMealDetailId, BigDecimal quantity);

    List<CalculateFoodResponse> calculateFeedForArea(Long areaId);

    void addFeedMealDetail(Long feedMealId, FeedMealDetailRequest request);

    void removeFeedMealDetail(Long feedMealId);
}
