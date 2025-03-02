package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.TaskTypeEntity;
import com.capstone.dfms.requests.TaskTypeRequest;
import com.capstone.dfms.services.ITaskTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.capstone.dfms.mappers.ITaskTypeMapper.INSTANCE;


import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/task_types")
@RequiredArgsConstructor
public class TaskTypeController {
    private final ITaskTypeService taskTypeService;

    @PostMapping("/create")
    public CoreApiResponse<?> createTaskType(
            @Valid @RequestBody TaskTypeRequest  request
    ){
        taskTypeService.createTaskType(INSTANCE.toModel(request));
        return CoreApiResponse.success("Create task type successfully.");
    }

    @GetMapping
    public CoreApiResponse<List<TaskTypeEntity>> getAll() {
        return CoreApiResponse.success(taskTypeService.getAllTaskTypes());
    }

    @GetMapping("/{id}")
    public CoreApiResponse<TaskTypeEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(taskTypeService.getTaskTypeById(id));
    }

    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteTaskType(
            @PathVariable Long id
    ){
        taskTypeService.deleteTaskType(id);
        return CoreApiResponse.success("Delete task type successfully");
    }
}
