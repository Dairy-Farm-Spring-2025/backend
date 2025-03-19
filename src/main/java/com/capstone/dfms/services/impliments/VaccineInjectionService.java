package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.mappers.IVaccineInjectionMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.VaccineInjectionRequest;
import com.capstone.dfms.services.IVaccineInjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineInjectionService implements IVaccineInjectionService {
    private final IVaccineInjectionRepository vaccineInjectionRepository;
    private final ICowRepository cowRepository;
    private final IVaccineCycleDetailRepository vaccineCycleDetailRepository;
    private final IVaccineInjectionMapper vaccineInjectionMapper;

    public VaccineInjectionEntity createVaccineInjection(VaccineInjectionRequest request) {
        // Validate cow
        CowEntity cow = cowRepository.findById(request.getCowId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Cow not found"));

        // Validate vaccine cycle detail
        VaccineCycleDetailEntity vaccineCycleDetail = vaccineCycleDetailRepository.findById(request.getVaccineCycleDetailId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Vaccine Cycle Detail not found"));

        UserEntity vet = UserStatic.getCurrentUser();

        // Convert request to entity
        VaccineInjectionEntity entity = vaccineInjectionMapper.toModel(request);
        entity.setCowEntity(cow);
        entity.setVaccineCycleDetail(vaccineCycleDetail);
        entity.setAdministeredBy(vet);

        // Save to DB
        return vaccineInjectionRepository.save(entity);
    }

    @Override
    public List<VaccineInjectionEntity> getAllVaccineInjections() {
        return vaccineInjectionRepository.findAll();
    }

    @Override
    public VaccineInjectionEntity getVaccineInjectionById(Long id) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Vaccine Injection not found"));

        return entity;
    }

    @Override
    public VaccineInjectionEntity updateVaccineInjection(Long id, VaccineInjectionRequest request) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Vaccine Injection not found"));

        entity.setInjectionDate(request.getInjectionDate());
        return null;
    }

    @Override
    public void deleteVaccineInjection(Long id) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Vaccine Injection not found"));

        vaccineInjectionRepository.delete(entity);
    }
}
