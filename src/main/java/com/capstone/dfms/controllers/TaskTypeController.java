package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.TaskTypeEntity;
import com.capstone.dfms.requests.TaskTypeRequest;
import com.capstone.dfms.services.ITaskTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.capstone.dfms.mappers.ITaskTypeMapper.INSTANCE;


import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/task_types")
@RequiredArgsConstructor
public class TaskTypeController {
    private final ITaskTypeService taskTypeService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<?> createTaskType(
            @Valid @RequestBody TaskTypeRequest  request
    ){
        taskTypeService.createTaskType(INSTANCE.toModel(request));
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.create_successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<TaskTypeEntity>> getAll() {
        return CoreApiResponse.success(taskTypeService.getAllTaskTypes());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/import")
    public CoreApiResponse<List<TaskTypeEntity>> getImport() {
        return CoreApiResponse.success(taskTypeService.getTaskTypesImport());
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<TaskTypeEntity> getById(@PathVariable Long id) {
        return CoreApiResponse.success(taskTypeService.getTaskTypeById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteTaskType(
            @PathVariable Long id
    ){
        taskTypeService.deleteTaskType(id);
        return CoreApiResponse.success(LocalizationUtils.getMessage("general.delete_successfully"));
    }
}
