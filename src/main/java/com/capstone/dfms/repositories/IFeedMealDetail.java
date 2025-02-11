package com.capstone.dfms.repositories;

import com.capstone.dfms.models.FeedMealDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFeedMealDetail extends JpaRepository<FeedMealDetailEntity, Long> {
}
