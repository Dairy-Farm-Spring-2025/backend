package com.capstone.dfms.services;

import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.requests.UpdateVaccineCycleRequest;
import com.capstone.dfms.requests.VaccineCycleRequest;
import com.capstone.dfms.requests.VaccineCycleUpdateInfo;

import java.util.List;

public interface IVaccineCycleService {
    VaccineCycleEntity createVaccineCycle(VaccineCycleRequest request);

    VaccineCycleEntity getVaccineCycleById(long id);

    List<VaccineCycleEntity> getAllVaccineCycles();

    VaccineCycleEntity updateVaccineCycle(Long id, UpdateVaccineCycleRequest request);

    void deleteVaccineCycle(long id);
}
