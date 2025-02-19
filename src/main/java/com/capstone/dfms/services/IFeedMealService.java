package com.capstone.dfms.services;

import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.requests.FeedMealRequest;

import java.util.List;

public interface IFeedMealService {

    FeedMealEntity createFeedMeal(FeedMealRequest request);

    FeedMealEntity getFeedMealById(long id);

    List<FeedMealEntity> getAllFeedMeals();

    void deleteFeedMeal(long id);

    double calculateDryMatter(CowStatus cowStatus, Long cowTypeId);
}
