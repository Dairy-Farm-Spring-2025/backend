package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.VaccineCycleEntity;
import com.capstone.dfms.models.WarehouseLocationEntity;
import com.capstone.dfms.requests.VaccineCycleRequest;
import com.capstone.dfms.requests.VaccineCycleUpdateInfo;
import com.capstone.dfms.requests.WarehouseUpdateRequest;
import com.capstone.dfms.services.IVaccineCycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IVaccineCycleMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/vaccinecycles")
@RequiredArgsConstructor
public class VaccineCycleController {
    private final IVaccineCycleService vaccineCycleService;
    @PostMapping("/create")
    public CoreApiResponse<VaccineCycleEntity> createVaccineCycle(@RequestBody VaccineCycleRequest request) {
        VaccineCycleEntity savedVaccineCycle = vaccineCycleService.createVaccineCycle(request);
        return CoreApiResponse.success(savedVaccineCycle,"Create Vaccine cycle successfully");
    }

    @GetMapping
    public CoreApiResponse<List<VaccineCycleEntity>> getAll() {
        return CoreApiResponse.success(vaccineCycleService.getAllVaccineCycles());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<VaccineCycleEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(vaccineCycleService.getVaccineCycleById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteWarehouse(
            @PathVariable Long id
    ){
        vaccineCycleService.deleteVaccineCycle(id);
        return CoreApiResponse.success("Delete vaccine cycle successfully");
    }

    @PutMapping("/{id}")
    public CoreApiResponse<?> updateWarehouse(
            @PathVariable Long id,
            @RequestBody VaccineCycleUpdateInfo request) {
        vaccineCycleService.updateVaccineCycle(id,request );
        return CoreApiResponse.success("Vaccine cylce update successfully.");
    }
}
