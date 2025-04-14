package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.mappers.IUseEquipmentMapper;
import com.capstone.dfms.models.EquipmentEntity;
import com.capstone.dfms.models.TaskTypeEntity;
import com.capstone.dfms.models.UseEquipmentEntity;
import com.capstone.dfms.models.compositeKeys.UseEquipmentPK;
import com.capstone.dfms.repositories.IEquipmentRepository;
import com.capstone.dfms.repositories.ITaskTypeRepository;
import com.capstone.dfms.repositories.IUseEquipmentRepository;
import com.capstone.dfms.requests.UseEquipmentEntityRequest;
import com.capstone.dfms.requests.UseEquipmentUpdateRequest;
import com.capstone.dfms.services.IUseEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UseEquipmentService implements IUseEquipmentService {
    private final IUseEquipmentRepository useEquipmentRepo;
    private final IEquipmentRepository equipmentRepo;
    private final ITaskTypeRepository taskTypeRepo;
    private final IUseEquipmentMapper mapper;

    @Override
    public List<UseEquipmentEntity> getAll() {
        return useEquipmentRepo.findAll();
    }

    @Override
    public UseEquipmentEntity getById(Long equipmentId, Long taskTypeId) {
        UseEquipmentPK pk = new UseEquipmentPK(equipmentId, taskTypeId);
        return useEquipmentRepo.findById(pk)
                .orElseThrow(() -> new RuntimeException("UseEquipment not found"));
    }

    @Override
    public UseEquipmentEntity create(UseEquipmentEntityRequest request) {
        EquipmentEntity equipment = equipmentRepo.findById(request.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        TaskTypeEntity taskType = taskTypeRepo.findById(request.getTaskTypeId())
                .orElseThrow(() -> new RuntimeException("TaskType not found"));

        Optional<UseEquipmentEntity> useEquipmentEntity = useEquipmentRepo.findById(new UseEquipmentPK(request.getEquipmentId(), request.getTaskTypeId()));
        if(useEquipmentEntity.isPresent()){
            throw new AppException(HttpStatus.BAD_REQUEST, "Duplicated equipment!");
        }

        UseEquipmentEntity entity = mapper.toModel(request);
        entity.setId(new UseEquipmentPK(request.getEquipmentId(), request.getTaskTypeId()));
        entity.setEquipment(equipment);
        entity.setTaskType(taskType);

        return useEquipmentRepo.save(entity);
    }

    @Override
    public UseEquipmentEntity update(Long equipmentId, Long taskTypeId, UseEquipmentUpdateRequest request) {
        UseEquipmentEntity existing = getById(equipmentId, taskTypeId);

        mapper.updateEntityFromDto(request, existing);

        return useEquipmentRepo.save(existing);
    }

    @Override
    public void delete(Long equipmentId, Long taskTypeId) {
        UseEquipmentPK pk = new UseEquipmentPK(equipmentId, taskTypeId);
        useEquipmentRepo.deleteById(pk);
    }
}
