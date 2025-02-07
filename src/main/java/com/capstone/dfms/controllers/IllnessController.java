package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.requests.IllnessCreateRequest;
import com.capstone.dfms.requests.IllnessPrognosisRequest;
import com.capstone.dfms.requests.IllnessReportRequest;
import com.capstone.dfms.requests.IllnessUpdateRequest;
import com.capstone.dfms.services.IIllnessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IIllnessMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/illness")
@RequiredArgsConstructor
public class IllnessController {
    private final  IIllnessService illnessService;

    @PreAuthorize("hasAnyRole('WORKER')")
    @PostMapping("/create")
    public CoreApiResponse<IllnessEntity> createIllness(@RequestBody IllnessCreateRequest request) {
        IllnessEntity illness = INSTANCE.toModel(request);
        return CoreApiResponse.success(illnessService.createIllness(illness));
    }

    @GetMapping
    public CoreApiResponse<List<IllnessEntity>> getAllIllnesses() {
        return CoreApiResponse.success(illnessService.getAllIllnesses());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<IllnessEntity> getIllnessById(@PathVariable Long id) {
        IllnessEntity illness = illnessService.getIllnessById(id);
        return CoreApiResponse.success(illness);
    }

    @GetMapping("/cow/{cowId}")
    public CoreApiResponse<List<IllnessEntity>> getIllnessesByCowId(@PathVariable Long cowId) {
        return CoreApiResponse.success((illnessService.getIllnessesByCowId(cowId)));
    }

    @PutMapping("/{id}")
    public CoreApiResponse<IllnessEntity> updateIllness(@PathVariable Long id, @RequestBody IllnessUpdateRequest updatedIllness) {
        return CoreApiResponse.success(illnessService.updateIllness(id, updatedIllness, false));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<Void> deleteIllness(@PathVariable Long id) {
        illnessService.deleteIllness(id);
        return CoreApiResponse.success("Delete successfully!");
    }

    @PreAuthorize("hasAnyRole('WORKER')")
    @PostMapping("/report")
    public CoreApiResponse<IllnessEntity> reportIllness(@RequestBody IllnessReportRequest request) {
        IllnessEntity illness = INSTANCE.toModel(request);
        return CoreApiResponse.success(illnessService.reportIllness(illness));
    }

    @PreAuthorize("hasAnyRole('VETERINARIANS')")
    @PutMapping("/prognosis/{id}")
    public CoreApiResponse<IllnessEntity> prognosisIllness(@PathVariable Long id, @RequestBody IllnessPrognosisRequest updatedIllness) {
        return CoreApiResponse.success(illnessService.prognosisIllness(id, updatedIllness));
    }
}
