package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IVaccineCycleMapper;
import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.VaccineCycleDetailEntity;
import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.repositories.ICowTypeRepository;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.repositories.IVaccineCycleDetailRepository;
import com.capstone.dfms.repositories.IVaccineCycleRepository;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.services.IVaccineCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VaccineCycleService implements IVaccineCycleService {

    private final IVaccineCycleRepository vaccineCycleRepository;

    private final IVaccineCycleMapper vaccineCycleMapper;

    private final IItemRepository itemRepository;

    private final IVaccineCycleDetailRepository vaccineCycleDetailRepository;

    private final ICowTypeRepository cowTypeRepository;


    @Override
    public VaccineCycleEntity createVaccineCycle(VaccineCycleRequest request) {
        CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Invalid Cow Type ID"));

        VaccineCycleEntity vaccineCycle = VaccineCycleEntity.builder()
                .name(StringUtils.NameStandardlizing(request.getName()))
                .description(request.getDescription())
                .cowTypeEntity(cowType)
                .build();

        if (request.getDetails() != null) {
            List<VaccineCycleDetailEntity> detailEntities = request.getDetails().stream()
                    .map(detailRequest -> mapToVaccineCycleDetail(detailRequest, vaccineCycle))
                    .collect(Collectors.toList());
            vaccineCycle.setVaccineCycleDetails(detailEntities);
        }

        return vaccineCycleRepository.save(vaccineCycle);
    }

    private VaccineCycleDetailEntity mapToVaccineCycleDetail(VaccineCycleDetailRequest detailRequest, VaccineCycleEntity vaccineCycle) {
        ItemEntity item = itemRepository.findById(detailRequest.getItemId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Invalid Item ID"));

        return VaccineCycleDetailEntity.builder()
                .name(detailRequest.getName())
                .description(detailRequest.getDescription())
                .dosageUnit(detailRequest.getDosageUnit())
                .dosage(detailRequest.getDosage())
                .injectionSite(detailRequest.getInjectionSite())
                .ageInMonths(detailRequest.getAgeInMonths())
                .itemEntity(item)
                .vaccineCycleEntity(vaccineCycle)
                .build();
    }

    @Override
    public VaccineCycleEntity getVaccineCycleById(long id) {
        return vaccineCycleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This vaccine cycle is not existed!"));
    }

    @Override
    public List<VaccineCycleEntity> getAllVaccineCycles() {
        return vaccineCycleRepository.findAll();
    }

//    @Override
//    public VaccineCycleEntity updateVaccineCycle(Long id, VaccineCycleUpdateInfo request) {
//        VaccineCycleEntity vaccineCycle = vaccineCycleRepository.findById(id)
//                .orElseThrow(() -> new DataNotFoundException("Vaccine cycle", "id", id));
//
//        vaccineCycleMapper.updateVaccineCycleFromRequest(request, vaccineCycle);
//        return vaccineCycleRepository.save(vaccineCycle);
//    }

    @Override
    public void deleteVaccineCycle(long id) {
        VaccineCycleEntity vaccineCycle = vaccineCycleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Vaccine cycle", "id", id));

        vaccineCycleRepository.delete(vaccineCycle);
    }

    @Override
    public VaccineCycleEntity updateVaccineCycle(Long id, UpdateVaccineCycleRequest request) {
        VaccineCycleEntity vaccineCycle = vaccineCycleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Vaccine Cycle", "id", id));

        vaccineCycle.setName(request.getName());
        vaccineCycle.setDescription(request.getDescription());

        if (request.getDetails() != null) {
            for (VaccineCycleDetailRequest detailReq : request.getDetails()) {
                ItemEntity item = itemRepository.findById(detailReq.getItemId())
                        .orElseThrow(() -> new DataNotFoundException("Item", "id", detailReq.getItemId()));

                VaccineCycleDetailEntity newDetail = new VaccineCycleDetailEntity();
                newDetail.setName(detailReq.getName());
                newDetail.setDescription(detailReq.getDescription());
                newDetail.setDosageUnit(detailReq.getDosageUnit());
                newDetail.setDosage(detailReq.getDosage());
                newDetail.setInjectionSite(detailReq.getInjectionSite());
                newDetail.setAgeInMonths(detailReq.getAgeInMonths());
                newDetail.setItemEntity(item);
                newDetail.setVaccineCycleEntity(vaccineCycle);

                vaccineCycle.getVaccineCycleDetails().add(newDetail);
            }
        }

        if (request.getUpdateDetail() != null) {
            for (UpdateVaccineCycleDetailRequest updateReq : request.getUpdateDetail()) {
                VaccineCycleDetailEntity existingDetail = vaccineCycleDetailRepository.findById(updateReq.getId())
                        .orElseThrow(() -> new DataNotFoundException("Vaccine Cycle Detail", "id", updateReq.getId()));

                existingDetail.setName(updateReq.getName());
                existingDetail.setDescription(updateReq.getDescription());
                existingDetail.setDosageUnit(updateReq.getDosageUnit());
                existingDetail.setDosage(updateReq.getDosage());
                existingDetail.setInjectionSite(updateReq.getInjectionSite());
                existingDetail.setAgeInMonths(updateReq.getAgeInMonths());

                ItemEntity item = itemRepository.findById(updateReq.getItemId())
                        .orElseThrow(() -> new DataNotFoundException("Item", "id", updateReq.getItemId()));
                existingDetail.setItemEntity(item);
            }
        }

        if (request.getDelete() != null) {
            for (Long detailId : request.getDelete()) {
                VaccineCycleDetailEntity detailToDelete = vaccineCycleDetailRepository.findById(detailId)
                        .orElseThrow(() -> new DataNotFoundException("Vaccine Cycle Detail", "id", detailId));
                vaccineCycleDetailRepository.delete(detailToDelete);
            }
        }

        return vaccineCycleRepository.save(vaccineCycle);
    }



}
