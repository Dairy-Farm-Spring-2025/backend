package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.requests.ApplicationTypeRequest;
import com.capstone.dfms.services.IApplicationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/application-type")
@RequiredArgsConstructor
public class ApplicationTypeController {
    private final IApplicationTypeService service;

    // Create a new ApplicationType
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public CoreApiResponse<ApplicationTypeEntity> createApplicationType(@RequestBody ApplicationTypeRequest request) {
        return CoreApiResponse.success(service.createApplicationType(request), LocalizationUtils.getMessage("general.create_successfully"));
    }

    // Get all ApplicationTypes
    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping
    public CoreApiResponse<List<ApplicationTypeEntity>> getAllApplicationTypes() {
        return  CoreApiResponse.success(service.getAllApplicationTypes());
    }

    // Get ApplicationType by ID
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<ApplicationTypeEntity> getApplicationTypeById(@PathVariable Long id) {
        return  CoreApiResponse.success(service.getApplicationTypeById(id));
    }

    // Update an ApplicationType
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<ApplicationTypeEntity> updateApplicationType(@PathVariable Long id, @RequestBody ApplicationTypeRequest request) {
        return  CoreApiResponse.success(service.updateApplicationType(id, request));
    }

    // Delete an ApplicationType
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteApplicationType(@PathVariable Long id) {
        service.deleteApplicationType(id);
        return  CoreApiResponse.success(LocalizationUtils.getMessage("general.delete_successfully"));
    }
}
