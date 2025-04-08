package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.VaccineCycleDetailEntity;
import com.capstone.dfms.requests.UpdateVaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailCreateRequest;
import com.capstone.dfms.requests.VaccineCycleDetailRequest;
import com.capstone.dfms.requests.VaccineCycleDetailUpdateRequest;
import com.capstone.dfms.services.IVaccineCycleDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/vaccine-cycle-details")
@RequiredArgsConstructor
public class VaccineCycleDetailController {
    private final IVaccineCycleDetailService service;

    @PostMapping
    public CoreApiResponse<VaccineCycleDetailEntity> create(@RequestBody VaccineCycleDetailCreateRequest request) {
        return CoreApiResponse.success(service.create(request));
    }

    @GetMapping
    public CoreApiResponse<List<VaccineCycleDetailEntity>> getAll() {
        return CoreApiResponse.success(service.getAll());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<VaccineCycleDetailEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(service.getById(id));
    }

    @PutMapping("/{id}")
    public CoreApiResponse<VaccineCycleDetailEntity> update(
            @PathVariable Long id,
            @RequestBody VaccineCycleDetailUpdateRequest request) {
        return CoreApiResponse.success(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return CoreApiResponse.success("Delete successfully!");
    }
}
