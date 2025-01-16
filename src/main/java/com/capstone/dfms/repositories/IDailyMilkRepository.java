package com.capstone.dfms.repositories;

import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.enums.MilkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface IDailyMilkRepository extends JpaRepository<DailyMilkEntity, Long> {
    @Query("SELECT d FROM DailyMilkEntity d WHERE d.cow.cowId = :cowId")
    List<DailyMilkEntity> findByCowId(@Param("cowId") Long cowId);

    @Query("SELECT d FROM DailyMilkEntity d " +
            "JOIN d.cow c " +
            "JOIN CowPenEntity cp ON cp.id.cowId = c.cowId " +
            "JOIN cp.penEntity p " +
            "JOIN p.areaBelongto a " +
            "WHERE (:cowId IS NULL OR c.cowId = :cowId) " +
            "AND (:areaId IS NULL OR a.areaId = :areaId) " +
            "AND d.milkDate = :milkDate " +
            "AND (cp.toDate IS NULL OR cp.toDate >= :milkDate)")
    List<DailyMilkEntity> searchDailyMilk(
            @Param("cowId") Long cowId,
            @Param("areaId") Long areaId,
            @Param("milkDate") LocalDate milkDate
    );


}
