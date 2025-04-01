package com.capstone.dfms.services;

import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.requests.UpdateTaskRequest;
import com.capstone.dfms.responses.TaskResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ITaskService {
    List<TaskEntity> createMultipleTasks(TaskRequest request);

    TaskEntity getTaskById(long id);

    List<TaskEntity> getAllTasks();

    void deleteTask(long id);

    Map<LocalDate, List<TaskResponse>> getTasksByDateRange(LocalDate startDate, LocalDate endDate);

    Map<LocalDate, List<TaskResponse>> getMyTasksByDateRange(LocalDate startDate, LocalDate endDate);

    TaskEntity getMyTaskById(Long taskId);

    List<TaskEntity> getMyTasks();

    TaskEntity updateTask(Long taskId, UpdateTaskRequest request);
}
