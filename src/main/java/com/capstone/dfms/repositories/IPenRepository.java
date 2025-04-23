package com.capstone.dfms.repositories;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.TokenEntity;
import com.capstone.dfms.models.enums.AreaType;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.PenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
@EnableJpaRepositories
public interface IPenRepository extends JpaRepository<PenEntity, Long> {
    boolean existsByName(String name);

    @Query("SELECT p FROM PenEntity p WHERE p.penId NOT IN (SELECT c.id.penId FROM CowPenEntity c WHERE c.toDate IS NULL OR c.toDate >= :currentDate)")
    List<PenEntity> findAvailablePens(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(cp) > 0 FROM CowPenEntity cp WHERE cp.penEntity.penId = :penId AND cp.id.fromDate <= :currentDate")
    boolean isOccupiedPen(@Param("penId") Long penId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(cp) > 0 FROM CowPenEntity cp WHERE cp.penEntity.penId = :penId AND cp.toDate >= :toDate")
    boolean isAvailablePen(@Param("penId") Long penId, @Param("toDate") LocalDate toDate);

    @Query("SELECT p FROM PenEntity p WHERE p.penStatus = :status")
    List<PenEntity> getPenWithStatus(@Param("status") PenStatus status);

    @Query("SELECT COUNT(p) FROM PenEntity p WHERE p.areaBelongto.areaId = :areaId AND p.penStatus = :status")
    long countPensByStatus(@Param("areaId") Long areaId, @Param("status") PenStatus status);

    @Query("SELECT p FROM PenEntity p WHERE p.areaBelongto.areaId = :areaId")
    List<PenEntity> findByArea(@Param("areaId") Long areaId);

    int countByAreaBelongto(AreaEntity areaBelongto);

    @Query("SELECT p FROM PenEntity p " +
            "WHERE (:cowTypeId IS NULL OR p.areaBelongto.cowTypeEntity.cowTypeId = :cowTypeId) " +
            "AND (:cowStatus IS NULL OR p.areaBelongto.cowStatus = :cowStatus) " +
            "AND p.areaBelongto.areaType = :areaType " +
            "AND p.penStatus = com.capstone.dfms.models.enums.PenStatus.empty")
    List<PenEntity> findAvailablePens(@Param("cowTypeId") Long cowTypeId,
                                      @Param("cowStatus") CowStatus cowStatus,
                                      @Param("areaType") AreaType areaType);


}
