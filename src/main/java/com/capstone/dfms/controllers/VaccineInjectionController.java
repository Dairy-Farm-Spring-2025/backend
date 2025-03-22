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
    @PreAuthorize("hasAnyRole('VETERINARIANS')")
    public CoreApiResponse<VaccineInjectionEntity> createVaccineInjection(@RequestBody VaccineInjectionRequest request) {
        VaccineInjectionEntity entity = vaccineInjectionService.createVaccineInjection(request);
        return CoreApiResponse.success(entity);
    }

    @GetMapping
    public ResponseEntity<List<VaccineInjectionEntity>> getAllVaccineInjections() {
        return ResponseEntity.ok(vaccineInjectionService.getAllVaccineInjections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VaccineInjectionEntity> getVaccineInjectionById(@PathVariable Long id) {
        return ResponseEntity.ok(vaccineInjectionService.getVaccineInjectionById(id));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<VaccineInjectionEntity> updateVaccineInjection(@PathVariable Long id, @RequestBody VaccineInjectionRequest request) {
//        return ResponseEntity.ok(vaccineInjectionService.updateVaccineInjection(id, request));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaccineInjection(@PathVariable Long id) {
        vaccineInjectionService.deleteVaccineInjection(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/report-injection")
    @PreAuthorize("hasAnyRole('VETERINARIANS')")
    public ResponseEntity<VaccineInjectionEntity> reportVaccineInjection(
            @PathVariable Long id,
            @RequestParam InjectionStatus status) { // Accept status as a query param

        VaccineInjectionEntity updatedEntity = vaccineInjectionService.reportVaccineInjection(id, status);
        return ResponseEntity.ok(updatedEntity);
    }
}
