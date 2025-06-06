package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.mappers.IIllnessMapper;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.models.enums.IllnessStatus;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.services.IIllnessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.capstone.dfms.mappers.IIllnessMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/illness")
@RequiredArgsConstructor
public class IllnessController {
    private final  IIllnessService illnessService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CoreApiResponse<IllnessEntity> createIllness(
            @Valid @ModelAttribute IllnessCreateRequest request,
            @RequestPart(name = "newImages", required = false) List<MultipartFile> newImages) throws IOException {
        return CoreApiResponse.success(illnessService.createIllnessForVet(request, newImages));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping
    public CoreApiResponse<List<IllnessEntity>> getAllIllnesses() {
        return CoreApiResponse.success(illnessService.getAllIllnesses());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/status")
    public CoreApiResponse<List<IllnessEntity>> getIllnessesByStatus(@RequestParam  IllnessStatus status) {
        return CoreApiResponse.success(illnessService.getIllnessByStatus(status));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{id}")
    public CoreApiResponse<IllnessEntity> getIllnessById(@PathVariable Long id) {
        IllnessEntity illness = illnessService.getIllnessWithDetail(id);
        return CoreApiResponse.success(illness);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/cow/{cowId}")
    public CoreApiResponse<List<IllnessEntity>> getIllnessesByCowId(@PathVariable Long cowId) {
        return CoreApiResponse.success((illnessService.getIllnessesByCowId(cowId)));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @PutMapping("/{id}")
    public CoreApiResponse<IllnessEntity> updateIllness(@PathVariable Long id, @RequestBody IllnessUpdateRequest updatedIllness) {
        return CoreApiResponse.success(illnessService.updateIllness(id, updatedIllness, false));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<Void> deleteIllness(@PathVariable Long id) {
        illnessService.deleteIllness(id);
        return CoreApiResponse.success("Delete successfully!");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WORKER')")
    @PostMapping(value = "/report", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CoreApiResponse<IllnessEntity> reportIllness(
            @Valid @ModelAttribute  IllnessReportRequest request,
            @RequestPart(name = "newImages", required = false) List<MultipartFile> newImages
    ) throws IOException {
        return CoreApiResponse.success(illnessService.reportIllness(INSTANCE.toModel(request), newImages));
    }


    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS')")
    @PutMapping("/prognosis/{id}")
    public CoreApiResponse<IllnessEntity> prognosisIllness(@PathVariable Long id, @RequestBody IllnessPrognosisRequest updatedIllness) {
        return CoreApiResponse.success(illnessService.prognosisIllness(id, updatedIllness));
    }


}
