package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.requests.TaskDateRangeRequest;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.requests.UpdateTaskRequest;
import com.capstone.dfms.responses.TaskResponse;
import com.capstone.dfms.services.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${app.api.version.v1}/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskService taskService;

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

    @PostMapping("/by-date-range")
    public CoreApiResponse<Map<LocalDate, List<TaskResponse>>> getTasksByDateRange(
            @RequestBody @Valid TaskDateRangeRequest request) {
        return CoreApiResponse.success(taskService.getTasksByDateRange(request.getFromDate(), request.getToDate()));
    }

    @GetMapping("/myTasks")
    public CoreApiResponse<List<TaskEntity>> getMyTasks() {
        List<TaskEntity> tasks = taskService.getMyTasks();
        return CoreApiResponse.success(tasks);
    }


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

    @PutMapping("update/{taskId}")
    public CoreApiResponse<TaskEntity> updateTask(@PathVariable Long taskId,
                                                 @RequestBody UpdateTaskRequest updateTaskRequest) {
        TaskEntity updatedTask = taskService.updateTask(taskId, updateTaskRequest);
        return CoreApiResponse.success(updatedTask,"Update task successfully");
    }
}
