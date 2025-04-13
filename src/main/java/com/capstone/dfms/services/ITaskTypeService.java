package com.capstone.dfms.services;

import com.capstone.dfms.models.TaskTypeEntity;

import java.util.List;

public interface ITaskTypeService {
    TaskTypeEntity createTaskType(TaskTypeEntity taskType );
    TaskTypeEntity getTaskTypeById(long id);
    List<TaskTypeEntity> getAllTaskTypes();
    void deleteTaskType(long id);
    List<TaskTypeEntity> getTaskTypesImport();
}
