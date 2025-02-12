package com.capstone.dfms.controllers;

import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.requests.IllnessDetailCreateRequest;
import com.capstone.dfms.requests.IllnessDetailPlanRequest;
import com.capstone.dfms.requests.IllnessDetailReportRequest;
import com.capstone.dfms.requests.IllnessDetailUpdateRequest;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.services.IIllnessDetailService;
import com.capstone.dfms.components.apis.CoreApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import static com.capstone.dfms.mappers.IIllnessDetailMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/illness-detail")
@RequiredArgsConstructor
public class IllnessDetailController {
    private final IIllnessDetailService illnessDetailService;

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping
    public CoreApiResponse<IllnessDetailEntity> createIllnessDetail(@RequestBody IllnessDetailCreateRequest detail) {
        return CoreApiResponse.success(illnessDetailService.createIllnessDetail(INSTANCE.toModel(detail), false));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping
    public CoreApiResponse<List<IllnessDetailEntity>> getAllIllnessDetails() {
        return CoreApiResponse.success(illnessDetailService.getAllIllnessDetails());
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{id}")
    public CoreApiResponse<IllnessDetailEntity> getIllnessDetailById(@PathVariable Long id) {
        return CoreApiResponse.success(illnessDetailService.getIllnessDetailById(id));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/illness/{illnessId}")
    public CoreApiResponse<List<IllnessDetailEntity>> getIllnessDetailsByIllnessId(@PathVariable Long illnessId) {
        return CoreApiResponse.success(illnessDetailService.getIllnessDetailsByIllnessId(illnessId));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("/{id}")
    public CoreApiResponse<IllnessDetailEntity> updateIllnessDetail(@PathVariable Long id, @RequestBody IllnessDetailUpdateRequest updatedDetail) {
        return CoreApiResponse.success(illnessDetailService.updateIllnessDetail(id, updatedDetail));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<Void> deleteIllnessDetail(@PathVariable Long id) {
        illnessDetailService.deleteIllnessDetail(id);
        return CoreApiResponse.success("Delete successfully!");
    }

    @PreAuthorize("hasAnyRole('VETERINARIANS')")
    @PostMapping("/create-plan")
    public CoreApiResponse<CowPenBulkResponse> createTreatmentPlan(@RequestBody List<IllnessDetailPlanRequest> detail) {
        return CoreApiResponse.success(illnessDetailService.createTreatmentPlan(detail));
    }

    @PreAuthorize("hasAnyRole('VETERINARIANS')")
    @PutMapping("/report-treatment/{id}")
    public CoreApiResponse<IllnessDetailEntity> reportTreatment(@PathVariable Long id, @RequestBody IllnessDetailReportRequest detail) {
        return CoreApiResponse.success(illnessDetailService.reportTreatment(id, detail));
    }

}
