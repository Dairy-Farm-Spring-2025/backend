package com.capstone.dfms.repositories;

import com.capstone.dfms.models.VaccineInjectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVaccineInjectionRepository extends JpaRepository<VaccineInjectionEntity, Long> {
}
