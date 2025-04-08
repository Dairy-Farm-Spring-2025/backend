package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.mappers.IVaccineCycleDetailMapper;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.VaccineCycleDetailEntity;
import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.repositories.IVaccineCycleDetailRepository;
import com.capstone.dfms.repositories.IVaccineCycleRepository;
import com.capstone.dfms.requests.UpdateVaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailCreateRequest;
import com.capstone.dfms.requests.VaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailUpdateRequest;
import com.capstone.dfms.services.IVaccineCycleDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineCycleDetailService implements IVaccineCycleDetailService {
    private final IVaccineCycleDetailRepository repository;
    private final IItemRepository itemRepository;
    private final IVaccineCycleDetailMapper mapper;
    private final IVaccineCycleRepository vaccineCycleRepository;

    @Override
    public VaccineCycleDetailEntity create(VaccineCycleDetailCreateRequest request) {
        VaccineCycleDetailEntity entity = mapper.toModel(request);

        VaccineCycleEntity vaccineCycle = vaccineCycleRepository.findById(request.getVaccineCycleID())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Vaccine cycle not found"));

        // Set related ItemEntity
        ItemEntity item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Item not found"));
        entity.setItemEntity(item);
        entity.setVaccineCycleEntity(vaccineCycle);

        return repository.save(entity);
    }

    @Override
    public VaccineCycleDetailEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "VaccineCycleDetail not found"));
    }

    @Override
    public List<VaccineCycleDetailEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public VaccineCycleDetailEntity update(Long id, VaccineCycleDetailUpdateRequest request) {
        VaccineCycleDetailEntity entity = repository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "VaccineCycleDetail not found"));

        mapper.updateEntityFromDto(request, entity);

        if (request.getItemId() != null) {
            ItemEntity item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Item not found"));
            entity.setItemEntity(item);
        }

        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        VaccineCycleDetailEntity entity = repository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "VaccineCycleDetail not found"));
        repository.delete(entity);
    }
}
