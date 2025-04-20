package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.VaccineInjectionEntity;
import com.capstone.dfms.models.enums.InjectionStatus;
import com.capstone.dfms.requests.VaccineInjectionRequest;
import com.capstone.dfms.services.IVaccineInjectionService;
import com.capstone.dfms.services.impliments.VaccineInjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/vaccine-injections")
@RequiredArgsConstructor
public class VaccineInjectionController {
    private final IVaccineInjectionService vaccineInjectionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    public CoreApiResponse<VaccineInjectionEntity> createVaccineInjection(@RequestBody VaccineInjectionRequest request) {
        VaccineInjectionEntity entity = vaccineInjectionService.createVaccineInjection(request);
        return CoreApiResponse.success(entity);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping
    public CoreApiResponse<List<VaccineInjectionEntity>> getAllVaccineInjections() {
        return CoreApiResponse.success(vaccineInjectionService.getAllVaccineInjections());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/{id}")
    public CoreApiResponse<VaccineInjectionEntity> getVaccineInjectionById(@PathVariable Long id) {
        return CoreApiResponse.success(vaccineInjectionService.getVaccineInjectionById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<Void> deleteVaccineInjection(@PathVariable Long id) {
        vaccineInjectionService.deleteVaccineInjection(id);
        return CoreApiResponse.success("");
    }

    @PutMapping("/{id}/report-injection")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    public CoreApiResponse<VaccineInjectionEntity> reportVaccineInjection(
            @PathVariable Long id,
            @RequestParam InjectionStatus status) { // Accept status as a query param

        VaccineInjectionEntity updatedEntity = vaccineInjectionService.reportVaccineInjection(id, status);
        return CoreApiResponse.success(updatedEntity);
    }
}
