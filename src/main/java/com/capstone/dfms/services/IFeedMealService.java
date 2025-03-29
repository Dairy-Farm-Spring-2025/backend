package com.capstone.dfms.services;

import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.requests.FeedMealRequest;
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

    public List<CalculateFoodResponse> calculateFeedForArea(Long areaId);
}
