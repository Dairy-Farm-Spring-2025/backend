package com.capstone.dfms.repositories;

import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.TokenEntity;
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

    @Query("SELECT p FROM PenEntity p WHERE p.penStatus = 'inPlaning'")
    List<PenEntity> getPenWithStatusInPlanning();
}
