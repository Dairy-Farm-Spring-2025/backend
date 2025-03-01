package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.DailyMilkEntity;
import com.capstone.dfms.models.MilkBatchEntity;
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
            "LEFT JOIN d.cow c " +
            "LEFT JOIN CowPenEntity cp ON cp.id.cowId = c.cowId " +
            "LEFT JOIN cp.penEntity p " +
            "LEFT JOIN p.areaBelongto a " +
            "WHERE (:cowId IS NULL OR c.cowId = :cowId) " +
            "AND (:areaId IS NULL OR a.areaId = :areaId) " +
            "AND (:shift IS NULL OR d.shift = :shift) " +
            "AND d.milkDate = CURRENT_DATE()")
    List<DailyMilkEntity> searchDailyMilk(
            @Param("cowId") Long cowId,
            @Param("areaId") Long areaId,
            @Param("shift") MilkShift shift
    );

    @Query("SELECT d FROM DailyMilkEntity d " +
            "LEFT JOIN d.cow c " +
            "LEFT JOIN CowPenEntity cp ON cp.id.cowId = c.cowId " +
            "LEFT JOIN cp.penEntity p " +
            "LEFT JOIN p.areaBelongto a " +
            "WHERE (:cowId IS NULL OR c.cowId = :cowId) " +
            "AND (:areaId IS NULL OR a.areaId = :areaId) " +
            "AND (:shift IS NULL OR d.shift = :shift) " +
            "AND d.milkDate = CURRENT_DATE() " +
            "AND (d.milkBatch IS NULL OR d.milkBatch.id IS NULL)")
    List<DailyMilkEntity> searchDailyMilkAvaible(
            @Param("cowId") Long cowId,
            @Param("areaId") Long areaId,
            @Param("shift") MilkShift shift
    );

    @Query("SELECT d FROM DailyMilkEntity d WHERE d.dailyMilkId IN :ids")
    List<DailyMilkEntity> findByDailyMilkIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT COUNT(dm) FROM DailyMilkEntity dm WHERE dm.cow = :cow AND dm.milkDate = :milkDate")
    long countByCowAndMilkDate(@Param("cow") CowEntity cow, @Param("milkDate") LocalDate milkDate);

    List<DailyMilkEntity> findByMilkBatch(MilkBatchEntity milkBatch);

    @Query("SELECT COALESCE(SUM(d.volume), 0) FROM DailyMilkEntity d " +
            "WHERE d.milkDate = :milkDate")
    Long getTotalMilkVolumeByDate(@Param("milkDate") LocalDate milkDate);

    @Query("SELECT MONTH(d.milkDate) AS month, SUM(d.volume) AS totalMilk " +
            "FROM DailyMilkEntity d " +
            "WHERE YEAR(d.milkDate) = :year " +
            "GROUP BY MONTH(d.milkDate) " +
            "ORDER BY month")
    List<Object[]> getTotalMilkByMonth(@Param("year") int year);

    @Query("SELECT d.cow.cowId AS cowId, MONTH(d.milkDate) AS month, SUM(d.volume) AS totalMilk " +
            "FROM DailyMilkEntity d " +
            "WHERE YEAR(d.milkDate) = :year " +
            "AND d.cow.cowId = :cowId " +
            "GROUP BY d.cow.cowId, MONTH(d.milkDate) " +
            "ORDER BY month")
    List<Object[]> getTotalMilkByMonthAndCow(@Param("year") int year, @Param("cowId") Long cowId);

    @Query("SELECT SUM(d.volume) " +
            "FROM DailyMilkEntity d " +
            "WHERE d.cow.cowId = :cowId " +
            "AND d.milkDate = :date")
    Long getTotalMilkByCowAndDate(@Param("cowId") Long cowId, @Param("date") LocalDate date);

    @Query("SELECT d FROM DailyMilkEntity d WHERE d.cow.cowId = :cowId " +
            "AND d.milkDate BETWEEN :startDate AND :endDate")
    List<DailyMilkEntity> findByCowIdAndMilkDateBetween(
            @Param("cowId") Long cowId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


}
