package com.capstone.dfms.repositories;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.CowPenEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.compositeKeys.CowPenPK;
import com.capstone.dfms.models.enums.PenCowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface ICowPenRepository extends JpaRepository<CowPenEntity, CowPenPK> {
    List<CowPenEntity> findByIdCowId(Long cowId);

    List<CowPenEntity> findByIdPenId(Long penId);



    @Query("SELECT c FROM CowPenEntity c WHERE c.id.cowId = :cowId " +
            "AND (c.toDate IS NULL OR c.toDate >= :currentDate)")
    List<CowPenEntity> findValidCowPensByCowId(@Param("cowId") Long cowId,
                                               @Param("currentDate") LocalDate currentDate);

    @Query("SELECT c FROM CowPenEntity c WHERE c.id.penId = :penId " +
            "AND (c.toDate IS NULL OR c.toDate >= :currentDate)")
    List<CowPenEntity> findValidCowPensByPenId(@Param("penId") Long penId,
                                               @Param("currentDate") LocalDate currentDate);

    @Query("SELECT c FROM CowPenEntity c " +
            "WHERE c.id.cowId = :cowId " +
            "ORDER BY c.toDate DESC LIMIT 1")
    CowPenEntity findPreviousCowPensByCowId(@Param("cowId") Long cowId);



    @Query("SELECT c FROM CowPenEntity c WHERE c.id.cowId = :cowId AND c.toDate IS NULL AND c.status = 'inPen'")
    Optional<CowPenEntity> findLatestCowPenByCowId(@Param("cowId") Long cowId);

    @Query("SELECT c FROM CowPenEntity c WHERE c.id.cowId = :cowId AND c.toDate IS NULL AND c.status = 'inPen'")
    CowPenEntity latestCowPenByCowId(@Param("cowId") Long cowId);

    @Query("SELECT c FROM CowPenEntity c WHERE c.id.penId = :penId AND c.toDate IS NULL AND c.status = 'inPen'")
    Optional<CowPenEntity> findCurrentCowInPen(@Param("penId") Long penId);

    @Query("SELECT c FROM CowPenEntity c WHERE c.id.penId = :penId AND c.status = 'waiting' ORDER BY c.id.fromDate ASC")
    Optional<CowPenEntity> findFirstWaitingCowInPen(@Param("penId") Long penId);

    @Query("SELECT c.cowEntity FROM CowPenEntity c WHERE c.penEntity.penId = :penId AND c.toDate IS NULL AND c.status = 'inPen'")
    List<CowEntity> findCowsByPenId(@Param("penId") Long penId);

    @Query("SELECT c FROM CowPenEntity c WHERE c.penEntity.penId IN :penIds AND c.status = 'inPen'")
    List<CowPenEntity> findActiveByPenIds(@Param("penIds") List<Long> penIds);

    @Query("SELECT COUNT(cp) > 0 FROM CowPenEntity cp WHERE cp.penEntity.areaBelongto = :area AND cp.status = :status")
    boolean existsByPenEntityAreaBelongtoAndStatus(@Param("area") AreaEntity area, @Param("status") PenCowStatus status);

}
