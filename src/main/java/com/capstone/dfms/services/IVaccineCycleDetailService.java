package com.capstone.dfms.services;

import com.capstone.dfms.models.VaccineCycleDetailEntity;
import com.capstone.dfms.requests.UpdateVaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailCreateRequest;
import com.capstone.dfms.requests.VaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailUpdateRequest;

import java.util.List;

public interface IVaccineCycleDetailService {
    VaccineCycleDetailEntity create(VaccineCycleDetailCreateRequest request);
    VaccineCycleDetailEntity getById(Long id);
    List<VaccineCycleDetailEntity> getAll();
    VaccineCycleDetailEntity update(Long id, VaccineCycleDetailUpdateRequest request);
    void delete(Long id);
}
