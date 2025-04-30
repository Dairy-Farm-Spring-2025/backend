package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.requests.ReportTaskRequest;
import com.capstone.dfms.requests.ReportTaskUpdateRequest;
import com.capstone.dfms.requests.ReviewReportTaskRequest;
import com.capstone.dfms.responses.ReportTaskResponse;
import com.capstone.dfms.services.IReportTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.capstone.dfms.mappers.IReportTaskMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/reportTask")
@RequiredArgsConstructor
public class ReportTaskController {
    private final IReportTaskService reportTaskService;

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @PostMapping("/joinTask/{taskId}")
    public CoreApiResponse<?> joinTask(
            @PathVariable Long taskId){
        reportTaskService.joinTask(taskId);
        return CoreApiResponse.success(LocalizationUtils.getMessage("report.task.check.in"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @PostMapping("/create/{id}")
    public CoreApiResponse<?> create(
            @PathVariable Long id,
            @Valid @ModelAttribute ReportTaskRequest request,
            @RequestParam(name = "imagesFile", required = false) List<MultipartFile> images) throws IOException {
        reportTaskService.createReportTask(id,INSTANCE.toModel(request),images);
        return CoreApiResponse.success(LocalizationUtils.getMessage("report.task.success"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS')")
    @PutMapping("/update/{id}")
    public CoreApiResponse<?> update(
            @PathVariable Long id,
            @Valid @ModelAttribute ReportTaskUpdateRequest request,
            @RequestParam(name = "newImages", required = false) List<MultipartFile> newImages) throws IOException {
        reportTaskService.updateReportTask(id, request, newImages);
        return CoreApiResponse.success(LocalizationUtils.getMessage("report.task.update"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/review/{id}")
    public CoreApiResponse<?> reviewReportTask(
            @PathVariable Long id,
            @Valid @RequestBody ReviewReportTaskRequest request)  {
        reportTaskService.reviewReportTask(id, request);
        return CoreApiResponse.success(LocalizationUtils.getMessage("report.task.review"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<ReportTaskEntity>> getAll() {
        return CoreApiResponse.success(reportTaskService.getAllReportTasks());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<ReportTaskEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(reportTaskService.getReportTaskById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> delete(
            @PathVariable Long id
    ){
        reportTaskService.deleteReportTask(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.create_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/task/{taskId}")
    public CoreApiResponse<?> getReportsByTask(@PathVariable Long taskId) {
        List<ReportTaskEntity> reports = reportTaskService.getReportsByTask(taskId);
        return CoreApiResponse.success(reports);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/task/{taskId}/date")
    public CoreApiResponse<?> getReportsByTaskAndDate(
            @PathVariable Long taskId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ReportTaskEntity report = reportTaskService.getReportsByTaskAndDate(taskId, date);
        return CoreApiResponse.success(report);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/by-date")
    public CoreApiResponse<List<ReportTaskResponse>> getReportTasksByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<ReportTaskResponse> reports = reportTaskService.getReportTasksByDate(date);
        return CoreApiResponse.success(reports);
    }
}
