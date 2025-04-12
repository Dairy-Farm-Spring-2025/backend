package com.capstone.dfms.services;

import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.requests.UpdateTaskRequest;
import com.capstone.dfms.responses.RangeTaskResponse;
import com.capstone.dfms.responses.TaskExcelResponse;
import com.capstone.dfms.responses.TaskResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ITaskService {
    List<TaskEntity> createMultipleTasks(TaskRequest request);

    TaskEntity getTaskById(long id);

    List<TaskEntity> getAllTasks();

    void deleteTask(long id);

    Map<LocalDate, List<TaskResponse>> getTasksByDateRange(LocalDate startDate, LocalDate endDate);

    Map<LocalDate, List<RangeTaskResponse>> getMyTasksByDateRange2(LocalDate startDate, LocalDate endDate);

    Map<LocalDate, List<TaskResponse>> getMyTasksByDateRange(LocalDate startDate, LocalDate endDate);



    TaskEntity getMyTaskById(Long taskId);

    List<TaskEntity> getMyTasks();

    TaskEntity updateTask(Long taskId, UpdateTaskRequest request);

    TaskEntity updateAssigneeForTask(Long taskId, Long assignee);

    RangeTaskResponse getTaskDetail(Long taskId);

    byte[] fillTemplateWithDropdown() throws IOException;

    Map<String, Map<String, List<TaskExcelResponse>>> importAndGroupTasks(MultipartFile file);
}
