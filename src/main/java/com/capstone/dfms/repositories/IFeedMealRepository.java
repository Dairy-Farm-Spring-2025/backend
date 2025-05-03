package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.models.FeedMealEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.FeedMealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IFeedMealRepository extends JpaRepository<FeedMealEntity, Long> {
    @Query("SELECT f FROM FeedMealEntity f WHERE f.cowTypeEntity = :cowType AND f.cowStatus = :cowStatus AND f.status = 'inUse'")
    Optional<FeedMealEntity> findByCowTypeAndStatus(
            @Param("cowType") CowTypeEntity cowType,
            @Param("cowStatus") CowStatus cowStatus
    );

    @Query("SELECT COUNT(f) > 0 FROM FeedMealEntity f WHERE f.cowStatus = :cowStatus AND f.cowTypeEntity = :cowTypeEntity AND f.status = :status")
    boolean existsByCowStatusAndCowTypeEntityAndStatus(
            @Param("cowStatus") CowStatus cowStatus,
            @Param("cowTypeEntity") CowTypeEntity cowTypeEntity,
            @Param("status") FeedMealStatus status
    );

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM FeedMealEntity f " +
            "WHERE f.cowTypeEntity.cowTypeId = :cowTypeId " +
            "AND f.cowStatus = :cowStatus " +
            "AND f.status = :status")
    boolean existsFeedMeal(
            @Param("cowTypeId") Long cowTypeId,
            @Param("cowStatus") CowStatus cowStatus,
            @Param("status") FeedMealStatus status);
}
