package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.requests.TaskDateRangeRequest;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.requests.UpdateTaskRequest;
import com.capstone.dfms.responses.RangeTaskResponse;
import com.capstone.dfms.responses.TaskResponse;
import com.capstone.dfms.services.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${app.api.version.v1}/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createTask(
            @Valid @RequestBody TaskRequest request
    ){
        taskService.createMultipleTasks(request);
        return CoreApiResponse.success("Create task successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<TaskEntity>> getAll() {
        return CoreApiResponse.success(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<TaskEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(taskService.getTaskById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteCategory(
            @PathVariable Long id
    ){
        taskService.deleteTask(id);
        return CoreApiResponse.success("Delete task successfully");
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping("/by-date-range")
    public CoreApiResponse<Map<LocalDate, List<TaskResponse>>> getTasksByDateRange(
            @RequestBody @Valid TaskDateRangeRequest request) {
        return CoreApiResponse.success(taskService.getTasksByDateRange(request.getFromDate(), request.getToDate()));
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/myTasks")
    public CoreApiResponse<List<TaskEntity>> getMyTasks() {
        List<TaskEntity> tasks = taskService.getMyTasks();
        return CoreApiResponse.success(tasks);
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping("/myTasks/by-date-range/mb")
    public CoreApiResponse<Map<LocalDate, List<RangeTaskResponse>>> getMyTasksByDateRange2(
            @RequestBody @Valid TaskDateRangeRequest request) {

        Map<LocalDate, List<RangeTaskResponse>> tasksByDateRange = taskService.getMyTasksByDateRange2(request.getFromDate(), request.getToDate());
        return CoreApiResponse.success(tasksByDateRange);
    }

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @PostMapping("/myTasks/by-date-range")
    public CoreApiResponse<Map<LocalDate, List<TaskResponse>>> getMyTasksByDateRange(
            @RequestBody @Valid TaskDateRangeRequest request) {

        Map<LocalDate, List<TaskResponse>> tasksByDateRange = taskService.getMyTasksByDateRange(request.getFromDate(), request.getToDate());
        return CoreApiResponse.success(tasksByDateRange);
    }



    @GetMapping("/myTasks/{taskId}")
    public CoreApiResponse<TaskEntity> getMyTaskById(@PathVariable Long taskId) {
        TaskEntity task = taskService.getMyTaskById(taskId);
        return CoreApiResponse.success(task);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("update/{taskId}")
    public CoreApiResponse<TaskEntity> updateTask(@PathVariable Long taskId,
                                                 @RequestBody UpdateTaskRequest updateTaskRequest) {
        TaskEntity updatedTask = taskService.updateTask(taskId, updateTaskRequest);
        return CoreApiResponse.success(updatedTask,"Update task successfully");
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PutMapping("/{taskId}/assign/{assigneeId}")
    public CoreApiResponse<TaskEntity> assginVeterinarians(@PathVariable Long taskId,
                                                           @PathVariable Long assigneeId) {
        TaskEntity updatedTask = taskService.updateAssigneeForTask(taskId, assigneeId);
        return CoreApiResponse.success(updatedTask,"Update task successfully");
    }


    @GetMapping("detail/mb/{taskId}")
    public CoreApiResponse<RangeTaskResponse> getTaskDetail(@PathVariable Long taskId) {
        RangeTaskResponse response = taskService.getTaskDetail(taskId);
        return CoreApiResponse.success(response);
    }

    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadExcelTemplate() throws IOException {
        byte[] excelBytes = taskService.fillTemplateWithDropdown();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Template_Task_Dairy_Farm.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
