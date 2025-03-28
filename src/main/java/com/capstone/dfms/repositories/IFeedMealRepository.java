package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.enums.CowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFeedMealRepository extends JpaRepository<FeedMealEntity, Long> {
    @Query("SELECT f FROM FeedMealEntity f WHERE f.cowTypeEntity = :cowType AND f.cowStatus = :cowStatus")
    Optional<FeedMealEntity> findByCowTypeAndStatus(@Param("cowType") CowTypeEntity cowType, @Param("cowStatus") CowStatus cowStatus);
}
