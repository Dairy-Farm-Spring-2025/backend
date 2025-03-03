package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.services.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
