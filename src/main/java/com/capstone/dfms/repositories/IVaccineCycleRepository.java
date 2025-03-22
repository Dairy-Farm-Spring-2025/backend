package com.capstone.dfms.repositories;

import com.capstone.dfms.models.VaccineCycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IVaccineCycleRepository extends JpaRepository<VaccineCycleEntity, Long> {
    @Query("SELECT v FROM VaccineCycleEntity v JOIN FETCH v.vaccineCycleDetails")
    List<VaccineCycleEntity> findAllWithDetails();
}
