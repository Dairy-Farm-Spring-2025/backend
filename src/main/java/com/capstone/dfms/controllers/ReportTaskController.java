package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.requests.ReportTaskRequest;
import com.capstone.dfms.requests.ReportTaskUpdateRequest;
import com.capstone.dfms.services.IReportTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping("/joinTask/{taskId}")
    public CoreApiResponse<?> joinTask(
            @PathVariable Long taskId){
        reportTaskService.joinTask(taskId);
        return CoreApiResponse.success("Join task successfully.");
    }

    @PostMapping("/create/{id}")
    public CoreApiResponse<?> create(
            @PathVariable Long id,
            @Valid @ModelAttribute ReportTaskRequest request,
            @RequestParam(name = "imagesFile", required = false) List<MultipartFile> images) throws IOException {
        reportTaskService.createReportTask(id,INSTANCE.toModel(request),images);
        return CoreApiResponse.success("Report task successfully.");
    }

    @PutMapping("/update/{id}")
    public CoreApiResponse<?> update(
            @PathVariable Long id,
            @Valid @ModelAttribute ReportTaskUpdateRequest request,
            @RequestParam(name = "newImages", required = false) List<MultipartFile> newImages) throws IOException {
        reportTaskService.updateReportTask(id, request, newImages);
        return CoreApiResponse.success("Report task updated successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<ReportTaskEntity>> getAll() {
        return CoreApiResponse.success(reportTaskService.getAllReportTasks());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<ReportTaskEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(reportTaskService.getReportTaskById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> delete(
            @PathVariable Long id
    ){
        reportTaskService.deleteReportTask(id);
        return CoreApiResponse.success("Delete report task successfully");
    }

    @GetMapping("/task/{taskId}")
    public CoreApiResponse<?> getReportsByTask(@PathVariable Long taskId) {
        List<ReportTaskEntity> reports = reportTaskService.getReportsByTask(taskId);
        return CoreApiResponse.success(reports);
    }

    @GetMapping("/task/{taskId}/date")
    public CoreApiResponse<?> getReportsByTaskAndDate(
            @PathVariable Long taskId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ReportTaskEntity> reports = reportTaskService.getReportsByTaskAndDate(taskId, date);
        return CoreApiResponse.success(reports);
    }
}
