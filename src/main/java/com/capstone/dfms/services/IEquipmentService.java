package com.capstone.dfms.services;

import com.capstone.dfms.models.EquipmentEntity;
import com.capstone.dfms.requests.EquipmentRequest;

import java.util.List;

public interface IEquipmentService {
    EquipmentEntity createEquipment(EquipmentRequest request);
    List<EquipmentEntity> getAllEquipments();
    EquipmentEntity getEquipmentById(Long id);
    EquipmentEntity updateEquipment(Long id, EquipmentRequest request);
    void deleteEquipment(Long id);
    List<EquipmentEntity> getByWarehouseLocationId(Long locationId);
}
