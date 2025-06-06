package com.capstone.dfms.services;

import com.capstone.dfms.models.FeedMealDetailEntity;
import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.requests.FeedMealDetailRequest;
import com.capstone.dfms.requests.FeedMealRequest;
import com.capstone.dfms.requests.UpdateFeedMealRequest;
import com.capstone.dfms.responses.CalculateFeedSummaryResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IFeedMealService {

    FeedMealEntity createFeedMeal(FeedMealRequest request);

    FeedMealEntity getFeedMealById(long id);

    List<FeedMealEntity> getAllFeedMeals();

    void deleteFeedMeal(long id);

    double calculateDryMatter(CowStatus cowStatus, Long cowTypeId);

    FeedMealEntity updateFeedMeal(Long feedMealId, UpdateFeedMealRequest request);

    FeedMealDetailEntity updateFeedMealDetail(Long feedMealDetailId, BigDecimal quantity);

    CalculateFeedSummaryResponse calculateFeedForArea(Long areaId);

    void addFeedMealDetail(Long feedMealId, FeedMealDetailRequest request);

    void removeFeedMealDetail(Long feedMealId);

    void isFeedMealInUse(Long cowTypeId, CowStatus cowStatus);
}
