package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.UseEquipmentEntity;
import com.capstone.dfms.requests.UseEquipmentEntityRequest;
import com.capstone.dfms.requests.UseEquipmentUpdateRequest;
import com.capstone.dfms.services.IUseEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/use-equipments")
@RequiredArgsConstructor
public class UseEquipmentController {
    private final IUseEquipmentService service;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping
    public CoreApiResponse<List<UseEquipmentEntity>> getAll() {
        return CoreApiResponse.success(service.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/{equipmentId}/{taskTypeId}")
    public CoreApiResponse<UseEquipmentEntity> getById(@PathVariable Long equipmentId,
                                                      @PathVariable Long taskTypeId) {
        return CoreApiResponse.success(service.getById(equipmentId, taskTypeId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public CoreApiResponse<UseEquipmentEntity> create(@RequestBody UseEquipmentEntityRequest request) {
        return CoreApiResponse.success(service.create(request), LocalizationUtils.getMessage("general.create_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @PutMapping("/{equipmentId}/{taskTypeId}")
    public CoreApiResponse<UseEquipmentEntity> update(@PathVariable Long equipmentId,
                                                     @PathVariable Long taskTypeId,
                                                     @RequestBody UseEquipmentUpdateRequest request) {
        return CoreApiResponse.success(service.update(equipmentId, taskTypeId, request),LocalizationUtils.getMessage("general.update_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @DeleteMapping("/{equipmentId}/{taskTypeId}")
    public CoreApiResponse<Void> delete(@PathVariable Long equipmentId,
                                       @PathVariable Long taskTypeId) {
        service.delete(equipmentId, taskTypeId);
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.delete_successfully"));
    }

}
