package com.capstone.dfms.repositories;

import com.capstone.dfms.models.FeedMealEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFeedMealRepository extends JpaRepository<FeedMealEntity, Long> {
}
