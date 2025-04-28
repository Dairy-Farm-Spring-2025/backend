package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.IVaccineInjectionMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.InjectionStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.VaccineInjectionRequest;
import com.capstone.dfms.services.IVaccineInjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.not.found")
                ));

        VaccineCycleDetailEntity vaccineCycleDetail = vaccineCycleDetailRepository.findById(request.getVaccineCycleDetailId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("vaccine_cycle_detail.not_found")));

        UserEntity vet = UserStatic.getCurrentUser();

        VaccineInjectionEntity entity = vaccineInjectionMapper.toModel(request);
        entity.setCowEntity(cow);
        entity.setVaccineCycleDetail(vaccineCycleDetail);
        entity.setAdministeredBy(vet);

        return vaccineInjectionRepository.save(entity);
    }

    @Override
    public List<VaccineInjectionEntity> getAllVaccineInjections() {
        return vaccineInjectionRepository.findAll();
    }

    @Override
    public VaccineInjectionEntity getVaccineInjectionById(Long id) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        return entity;
    }

    @Override
    public VaccineInjectionEntity updateVaccineInjection(Long id, VaccineInjectionRequest request) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        entity.setInjectionDate(request.getInjectionDate());
        return null;
    }

    @Override
    public void deleteVaccineInjection(Long id) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        vaccineInjectionRepository.delete(entity);
    }

    @Override
    public VaccineInjectionEntity reportVaccineInjection(Long id, InjectionStatus status) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        if(entity.getInjectionDate().equals(LocalDate.now())){
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("report.no_time"));
        }

        entity.setAdministeredBy(UserStatic.getCurrentUser());
        entity.setStatus(status);
        return vaccineInjectionRepository.save(entity);
    }


}
