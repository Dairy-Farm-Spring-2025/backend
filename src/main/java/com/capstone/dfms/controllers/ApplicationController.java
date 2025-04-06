package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.ApplicationCreateRequest;
import com.capstone.dfms.services.IApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${app.api.version.v1}/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final IApplicationService applicationService;
    private final MessageSource messageSource;  // Inject MessageSource

    @PreAuthorize("hasAnyRole('WORKER','VETERINARIANS')")
    @PostMapping("/request")
    public CoreApiResponse<ApplicationEntity> requestApplication(@RequestBody ApplicationCreateRequest request) {
        ApplicationEntity application = applicationService.createApplication(request);
        String message = messageSource.getMessage("application.request.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(application, message);
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{id}")
    public CoreApiResponse<ApplicationEntity> getApplicationById(@PathVariable Long id) {
        ApplicationEntity application = applicationService.getApplicationById(id);
        String message = messageSource.getMessage("application.fetch.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(application, message);
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping()
    public CoreApiResponse<List<ApplicationEntity>> getApplication() {
        List<ApplicationEntity> applications = applicationService.getApplications();
        String message = messageSource.getMessage("applications.fetch.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(applications, message);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("/approval-request/{id}")
    public CoreApiResponse<ApplicationEntity> updateApplication(@PathVariable Long id, @RequestBody ApplicationApproveRequest request) {
        ApplicationEntity application = applicationService.updateApplication(id, request);
        String message = messageSource.getMessage("application.update.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(application, message);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        String message = messageSource.getMessage("application.delete.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(message);
    }

    @PreAuthorize("hasAnyRole('WORKER','VETERINARIANS','MANAGER')")
    @PutMapping("/cancel-request/{id}")
    public CoreApiResponse<ApplicationEntity> cancelApplication(@PathVariable Long id, @RequestBody ApplicationApproveRequest request) {
        ApplicationEntity application = applicationService.cancelApplication(id, request);
        String message = messageSource.getMessage("application.cancel.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(application, message);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/by-type/{typeId}")
    public CoreApiResponse<List<ApplicationEntity>> getApplicationsByApplicationType(@PathVariable Long typeId) {
        List<ApplicationEntity> applications = applicationService.getApplicationsByApplicationType(typeId);
        String message = messageSource.getMessage("applications.byType.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(applications, message);
    }

    @PreAuthorize("hasAnyRole('WORKER','VETERINARIANS')")
    @GetMapping("/my-request")
    public CoreApiResponse<List<ApplicationEntity>> getApplicationsByRequestBy() {
        List<ApplicationEntity> applications = applicationService.getApplicationsByRequestBy();
        String message = messageSource.getMessage("applications.myRequests.success", null, LocaleContextHolder.getLocale());
        return CoreApiResponse.success(applications, message);
    }

    @GetMapping("/findApplication")
    public CoreApiResponse<ApplicationEntity> getApplicationsByUserAndType(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        ApplicationEntity applications = applicationService.getApplicationsByUserDateAndType(userId, fromDate, toDate);
        return CoreApiResponse.success(applications);
    }
}
