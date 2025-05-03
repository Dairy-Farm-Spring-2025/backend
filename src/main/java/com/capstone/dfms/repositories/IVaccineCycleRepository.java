package com.capstone.dfms.repositories;

import com.capstone.dfms.models.VaccineCycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IVaccineCycleRepository extends JpaRepository<VaccineCycleEntity, Long> {
    @Query("SELECT v FROM VaccineCycleEntity v JOIN FETCH v.vaccineCycleDetails")
    List<VaccineCycleEntity> findAllWithDetails();

    @Query("SELECT v FROM VaccineCycleEntity v WHERE v.cowTypeEntity.cowTypeId = :cowTypeId")
    List<VaccineCycleEntity> findByCowTypeId(@Param("cowTypeId") Long cowTypeId);

    @Query("SELECT v FROM VaccineCycleEntity v WHERE v.cowTypeEntity.cowTypeId = :cowTypeId")
    Optional<List<VaccineCycleEntity>> findByCowTypeIdOptional(@Param("cowTypeId") Long cowTypeId);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END " +
            "FROM VaccineCycleEntity v WHERE v.cowTypeEntity.cowTypeId = :cowTypeId")
    boolean existsByCowTypeId(@Param("cowTypeId") Long cowTypeId);
}
