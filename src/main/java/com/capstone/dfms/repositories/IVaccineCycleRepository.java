package com.capstone.dfms.repositories;

import com.capstone.dfms.models.VaccineCycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVaccineCycleRepository extends JpaRepository<VaccineCycleEntity, Long> {
}
