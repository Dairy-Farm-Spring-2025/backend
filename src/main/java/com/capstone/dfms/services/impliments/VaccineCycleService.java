package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IVaccineCycleDetailMapper;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VaccineCycleService implements IVaccineCycleService {

    private final IVaccineCycleRepository vaccineCycleRepository;

    private final IVaccineCycleMapper vaccineCycleMapper;

    private final IItemRepository itemRepository;

    private final IVaccineCycleDetailRepository vaccineCycleDetailRepository;

    private final ICowTypeRepository cowTypeRepository;
    private final IVaccineCycleDetailMapper vaccineCycleDetailMapper;


    @Override
    public VaccineCycleEntity createVaccineCycle(VaccineCycleRequest request) {
        CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow_type.not_found")));

        Optional<List<VaccineCycleEntity>> cowTypeIdOptional = vaccineCycleRepository.findByCowTypeIdOptional(cowType.getCowTypeId());
        if(!cowTypeIdOptional.get().isEmpty()){
            throw new AppException(HttpStatus.BAD_REQUEST, "There are Vaccine Cycle for " + cowType.getName());
        }

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
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_exist")));

        if(!(item.getCategoryEntity().getName().equalsIgnoreCase("Vaccine") ||
                item.getCategoryEntity().getName().equalsIgnoreCase("Vắc-xin"))){
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_vaccine"));
        }

        VaccineCycleDetailEntity entity = vaccineCycleDetailMapper.toModel(detailRequest);
        entity.setVaccineCycleEntity(vaccineCycle);
        entity.setItemEntity(item);

        return entity;
    }

    @Override
    public VaccineCycleEntity getVaccineCycleById(long id) {
        return vaccineCycleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("vaccine_cycle.not_found")));
    }

    @Override
    public List<VaccineCycleEntity> getAllVaccineCycles() {
        return vaccineCycleRepository.findAll();
    }


    @Override
    public void deleteVaccineCycle(long id) {
        VaccineCycleEntity vaccineCycle = vaccineCycleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("vaccine_cycle.not_found")));

        vaccineCycleRepository.delete(vaccineCycle);
    }

    @Override
    public VaccineCycleEntity updateVaccineCycle(Long id, UpdateVaccineCycleRequest request) {
        VaccineCycleEntity vaccineCycle = vaccineCycleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("vaccine_cycle.not_found")));

        vaccineCycle.setName(request.getName());
        vaccineCycle.setDescription(request.getDescription());

        if (request.getDetails() != null) {
            for (VaccineCycleDetailRequest detailReq : request.getDetails()) {
                vaccineCycle.getVaccineCycleDetails().add(mapToVaccineCycleDetail(detailReq, vaccineCycle));
            }
        }

        if (request.getUpdateDetail() != null) {
            for (UpdateVaccineCycleDetailRequest updateReq : request.getUpdateDetail()) {
                VaccineCycleDetailEntity existingDetail = vaccineCycleDetailRepository.findById(updateReq.getId())
                        .orElseThrow(() -> new DataNotFoundException("Vaccine Cycle Detail", "id", updateReq.getId()));

                if(updateReq.getItemId() != null) {
                    ItemEntity item = itemRepository.findById(updateReq.getItemId())
                            .orElseThrow(() -> new DataNotFoundException("Item", "id", updateReq.getItemId()));
                    existingDetail.setItemEntity(item);
                }

                vaccineCycleDetailMapper.updateEntityFromDto(updateReq, existingDetail);
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

    @Override
    public List<VaccineCycleEntity> getByCowTypeId(Long cowTypeId) {
        return vaccineCycleRepository.findByCowTypeId(cowTypeId);
    }

    @Override
    public void validateNoExistingVaccineCycle(Long cowTypeId) {
        if (vaccineCycleRepository.existsByCowTypeId(cowTypeId)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("vaccine_cycle_exist"));
        }
    }

}
