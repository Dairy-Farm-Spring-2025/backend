package com.capstone.dfms.services;

import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.models.VaccineInjectionEntity;
import com.capstone.dfms.models.enums.InjectionStatus;
import com.capstone.dfms.requests.VaccineInjectionRequest;

import java.util.List;

public interface IVaccineInjectionService {
    void createVaccineInjection();
    List<VaccineInjectionEntity> getAllVaccineInjections();
    VaccineInjectionEntity getVaccineInjectionById(Long id);
    VaccineInjectionEntity updateVaccineInjection(Long id, VaccineInjectionRequest request);
    void deleteVaccineInjection(Long id);

    VaccineInjectionEntity reportVaccineInjection(Long id, InjectionStatus status);
}
