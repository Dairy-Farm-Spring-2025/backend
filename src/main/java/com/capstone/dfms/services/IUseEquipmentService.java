package com.capstone.dfms.services;

import com.capstone.dfms.models.UseEquipmentEntity;
import com.capstone.dfms.requests.UseEquipmentEntityRequest;
import com.capstone.dfms.requests.UseEquipmentUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface IUseEquipmentService {
    List<UseEquipmentEntity> getAll();
    UseEquipmentEntity getById(Long equipmentId, Long taskTypeId);
    UseEquipmentEntity create(UseEquipmentEntityRequest request);
    UseEquipmentEntity update(Long equipmentId, Long taskTypeId, UseEquipmentUpdateRequest request);
    void delete(Long equipmentId, Long taskTypeId);
}
