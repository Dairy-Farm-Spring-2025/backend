package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.ApplicationCreateRequest;
import com.capstone.dfms.services.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final IApplicationService applicationService;

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @PostMapping("/request")
    public CoreApiResponse<ApplicationEntity> requestApplication(@RequestBody ApplicationCreateRequest request) {
        ApplicationEntity application = applicationService.createApplication(request);
        return CoreApiResponse.success(application, LocalizationUtils.getMessage("general.create_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{id}")
    public CoreApiResponse<ApplicationEntity> getApplicationById(@PathVariable Long id) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        return CoreApiResponse.success(application);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','MANAGER','VETERINARIANS')")
    @GetMapping()
    public CoreApiResponse<List<ApplicationEntity>> getApplication() {
        List<ApplicationEntity> applications = applicationService.getApplications();
        return CoreApiResponse.success(applications);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/approval-request/{id}")
    public CoreApiResponse<ApplicationEntity> updateApplication(@PathVariable Long id, @RequestBody ApplicationApproveRequest request) {
        ApplicationEntity application = applicationService.updateApplication(id, request);
        return CoreApiResponse.success(application, LocalizationUtils.getMessage("general.update_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.delete_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @PutMapping("/cancel-request/{id}")
    public CoreApiResponse<ApplicationEntity> cancelApplication(@PathVariable Long id, @RequestBody ApplicationApproveRequest request) {
        ApplicationEntity application = applicationService.cancelApplication(id, request);
        return CoreApiResponse.success(application, LocalizationUtils.getMessage("application.cancel.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/by-type/{typeId}")
    public CoreApiResponse<List<ApplicationEntity>> getApplicationsByApplicationType(@PathVariable Long typeId) {
        List<ApplicationEntity> applications = applicationService.getApplicationsByApplicationType(typeId);
        return CoreApiResponse.success(applications);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @GetMapping("/my-request")
    public CoreApiResponse<List<ApplicationEntity>> getApplicationsByRequestBy() {
        List<ApplicationEntity> applications = applicationService.getApplicationsByRequestBy();
        return CoreApiResponse.success(applications);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/findApplication")
    public CoreApiResponse<ApplicationEntity> getApplicationsByUserAndType(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        ApplicationEntity applications = applicationService.getApplicationsByUserDateAndType(userId, fromDate, toDate);
        return CoreApiResponse.success(applications);
    }
}
