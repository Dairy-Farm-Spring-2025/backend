package com.capstone.dfms.repositories;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ICowRepository extends JpaRepository<CowEntity, Long> {
    boolean existsByName(String name);

    @Query("SELECT COUNT(cp) = 0 FROM CowPenEntity cp WHERE cp.cowEntity.id = :cowId AND (cp.toDate IS NULL OR cp.toDate > :currentDate)")
    boolean isCowNotInAnyPen(@Param("cowId") Long cowId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(c) FROM CowEntity c WHERE c.name LIKE %:substring%")
    long countByNameContains(@Param("substring") String substring);

    List<CowEntity> findByCowTypeEntity_CowTypeIdAndDateOfOutIsNullOrDateOfOutAfter(Long cowTypeId, LocalDate currentDate);
    Optional<CowEntity> findByName(String name);
    @Query("SELECT MAX(c.importTimes) FROM CowEntity c")
    Long getMaxImportTimes();
}
