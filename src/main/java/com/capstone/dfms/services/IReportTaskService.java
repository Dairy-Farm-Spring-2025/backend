package com.capstone.dfms.services;

import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.requests.ReportTaskUpdateRequest;
import com.capstone.dfms.requests.ReviewReportTaskRequest;
import com.capstone.dfms.responses.ReportTaskResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface IReportTaskService {
    ReportTaskEntity createReportTask(long reportTaskId,ReportTaskEntity updatedReportTask,List<MultipartFile> images) throws IOException;

    ReportTaskEntity getReportTaskById(long id);

    List<ReportTaskEntity> getAllReportTasks();

    ReportTaskEntity updateReportTask(Long id, ReportTaskUpdateRequest request, List<MultipartFile> newImages) throws IOException;

    void deleteReportTask(long id);

    ReportTaskEntity joinTask(long taskId);

    List<ReportTaskEntity> getReportsByTask(Long taskId);

    ReportTaskEntity getReportsByTaskAndDate(Long taskId, LocalDate date);

    ReportTaskEntity reviewReportTask(Long id, ReviewReportTaskRequest request);

    List<ReportTaskResponse> getReportTasksByDate(LocalDate date);
}
