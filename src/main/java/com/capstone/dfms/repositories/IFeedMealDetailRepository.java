package com.capstone.dfms.repositories;

import com.capstone.dfms.models.FeedMealDetailEntity;
import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IFeedMealDetailRepository extends JpaRepository<FeedMealDetailEntity, Long> {
    @Query("SELECT f FROM FeedMealDetailEntity f WHERE f.feedMealEntity = :feedMeal AND f.itemEntity = :item")
    Optional<FeedMealDetailEntity> findByFeedMealEntityAndItemEntity(@Param("feedMeal") FeedMealEntity feedMeal,
                                                                     @Param("item") ItemEntity item);
}
