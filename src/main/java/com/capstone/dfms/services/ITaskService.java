package com.capstone.dfms.services;

import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.requests.TaskRequest;

import java.util.List;

public interface ITaskService {
    List<TaskEntity> createMultipleTasks(TaskRequest request);

    TaskEntity getTaskById(long id);

    List<TaskEntity> getAllTasks();

    void deleteTask(long id);
}
