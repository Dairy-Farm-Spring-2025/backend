package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.ApplicationCreateRequest;
import com.capstone.dfms.services.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final IApplicationService applicationService;

    @PreAuthorize("hasAnyRole('WORKER','VETERINARIANS')")
    @PostMapping("/request")
    public CoreApiResponse<ApplicationEntity> requestApplication(@RequestBody ApplicationCreateRequest request) {
        return CoreApiResponse.success(applicationService.createApplication(request));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{id}")
    public CoreApiResponse<ApplicationEntity> getApplicationById(@PathVariable Long id) {
        return CoreApiResponse.success(applicationService.getApplicationById(id));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping()
    public CoreApiResponse<List<ApplicationEntity>> getApplication() {
        return CoreApiResponse.success(applicationService.getApplications());
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("/approval-request/{id}")
    public CoreApiResponse<ApplicationEntity> updateApplication(@PathVariable Long id, @RequestBody ApplicationApproveRequest request) {
        return CoreApiResponse.success(applicationService.updateApplication(id, request));
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return CoreApiResponse.success("Delete successfully!");
    }

    @PreAuthorize("hasAnyRole('WORKER','VETERINARIANS','MANAGER')")
    @PutMapping("/cancel-request/{id}")
    public CoreApiResponse<ApplicationEntity> cancelApplication(@PathVariable Long id, @RequestBody ApplicationApproveRequest request) {
        return CoreApiResponse.success(applicationService.cancelApplication(id, request));
    }

//    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/by-type/{typeId}")
    public CoreApiResponse<List<ApplicationEntity>> getApplicationsByApplicationType(@PathVariable Long typeId) {
        return CoreApiResponse.success(applicationService.getApplicationsByApplicationType(typeId));
    }

    @PreAuthorize("hasAnyRole('WORKER','VETERINARIANS')")
    @GetMapping("/my-request")
    public CoreApiResponse<List<ApplicationEntity>> getApplicationsByRequestBy() {
        return CoreApiResponse.success(applicationService.getApplicationsByRequestBy());
    }
}
