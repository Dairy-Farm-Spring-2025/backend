package com.capstone.dfms.services;

import com.capstone.dfms.models.VaccineInjectionEntity;
import com.capstone.dfms.requests.VaccineInjectionRequest;

import java.util.List;

public interface IVaccineInjectionService {
    VaccineInjectionEntity createVaccineInjection(VaccineInjectionRequest request);
    List<VaccineInjectionEntity> getAllVaccineInjections();
    VaccineInjectionEntity getVaccineInjectionById(Long id);
    VaccineInjectionEntity updateVaccineInjection(Long id, VaccineInjectionRequest request);
    void deleteVaccineInjection(Long id);
}
