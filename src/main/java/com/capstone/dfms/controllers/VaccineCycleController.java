package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.requests.UpdateVaccineCycleRequest;
import com.capstone.dfms.requests.VaccineCycleRequest;
import com.capstone.dfms.requests.VaccineCycleUpdateInfo;
import com.capstone.dfms.requests.WarehouseUpdateRequest;
import com.capstone.dfms.services.IVaccineCycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IVaccineCycleMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/vaccinecycles")
@RequiredArgsConstructor
public class VaccineCycleController {
    private final IVaccineCycleService vaccineCycleService;
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    @PostMapping("/create")
    public CoreApiResponse<VaccineCycleEntity> createVaccineCycle(@RequestBody VaccineCycleRequest request) {
        VaccineCycleEntity savedVaccineCycle = vaccineCycleService.createVaccineCycle(request);
        return CoreApiResponse.success(savedVaccineCycle,"Create Vaccine cycle successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping
    public CoreApiResponse<List<VaccineCycleEntity>> getAll() {
        return CoreApiResponse.success(vaccineCycleService.getAllVaccineCycles());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/{id}")
    public CoreApiResponse<VaccineCycleEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(vaccineCycleService.getVaccineCycleById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteVaccineCycle(
            @PathVariable Long id
    ){
        vaccineCycleService.deleteVaccineCycle(id);
        return CoreApiResponse.success("Delete vaccine cycle successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    @PutMapping("/{id}")
    public CoreApiResponse<VaccineCycleEntity> updateVaccineCycle(@PathVariable Long id,
                                                                  @RequestBody @Valid UpdateVaccineCycleRequest request) {
        return CoreApiResponse.success(vaccineCycleService.updateVaccineCycle(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/cowType/{cowTypeId}")
    public CoreApiResponse<List<VaccineCycleEntity>> getByCowType(@PathVariable Long cowTypeId) {
        List<VaccineCycleEntity> vaccineCycles = vaccineCycleService.getByCowTypeId(cowTypeId);
        return CoreApiResponse.success(vaccineCycles);
    }
}
