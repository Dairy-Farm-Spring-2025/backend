package com.capstone.dfms.repositories;

import com.capstone.dfms.models.IllnessDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IIllnessDetailRepository extends JpaRepository<IllnessDetailEntity, Long> {
    List<IllnessDetailEntity> findByIllnessEntityIllnessId(Long illnessId);
    @Query("SELECT i FROM IllnessDetailEntity i WHERE i.illnessEntity.illnessId = :illnessId AND i.date < :date")
    List<IllnessDetailEntity> findDetailsByIllnessIdAndDateAfter(@Param("illnessId") Long illnessId, @Param("date") LocalDate date);

    @Query("SELECT i FROM IllnessDetailEntity i WHERE i.date BETWEEN :startDate AND :endDate")
    List<IllnessDetailEntity> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
