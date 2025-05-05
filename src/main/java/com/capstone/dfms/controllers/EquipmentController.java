package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.EquipmentEntity;
import com.capstone.dfms.requests.EquipmentRequest;
import com.capstone.dfms.services.IEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    private final IEquipmentService equipmentService;

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping
    public CoreApiResponse<EquipmentEntity> createEquipment(@RequestBody EquipmentRequest request) {
        EquipmentEntity equipment = equipmentService.createEquipment(request);
        return CoreApiResponse.success(equipment,LocalizationUtils.getMessage("general.create_successfully"));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping
    public CoreApiResponse<List<EquipmentEntity>> getAllEquipments() {
        List<EquipmentEntity> equipments = equipmentService.getAllEquipments();
        return CoreApiResponse.success(equipments);
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{id}")
    public CoreApiResponse<EquipmentEntity> getEquipmentById(@PathVariable Long id) {
        EquipmentEntity equipment = equipmentService.getEquipmentById(id);
        return CoreApiResponse.success(equipment);
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("/{id}")
    public CoreApiResponse<EquipmentEntity> updateEquipment(
            @PathVariable Long id,
            @RequestBody EquipmentRequest request) {
        EquipmentEntity updatedEquipment = equipmentService.updateEquipment(id, request);
        return CoreApiResponse.success(updatedEquipment,LocalizationUtils.getMessage("general.update_successfully"));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.delete_successfully"));
    }

    @GetMapping("/location/{locationId}")
    public CoreApiResponse<?> getByLocation(@PathVariable Long locationId) {
        return CoreApiResponse.success(equipmentService.getByWarehouseLocationId(locationId));
    }
}
