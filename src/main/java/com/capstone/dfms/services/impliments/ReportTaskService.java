package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.constants.ImageContants;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.components.utils.UploadImagesUtils;
import com.capstone.dfms.mappers.IReportTaskMapper;
import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.models.ReportTaskImageEntity;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.ReportStatus;
import com.capstone.dfms.repositories.IReportTaskImageRepository;
import com.capstone.dfms.repositories.IReportTaskRepository;
import com.capstone.dfms.repositories.ITaskRepository;
import com.capstone.dfms.requests.ReportTaskUpdateRequest;
import com.capstone.dfms.requests.ReviewReportTaskRequest;
import com.capstone.dfms.responses.ReportTaskResponse;
import com.capstone.dfms.services.IReportTaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReportTaskService implements IReportTaskService {
    private final IReportTaskRepository reportTaskRepository;
    private final ITaskRepository taskRepository;
    private final IReportTaskImageRepository reportTaskImageRepository;
    private final IReportTaskMapper reportTaskMapper;

    @Override
    public ReportTaskEntity createReportTask(long reportTaskId,ReportTaskEntity updatedReportTask,List<MultipartFile> images) throws IOException {
        ReportTaskEntity existingReport = reportTaskRepository.findById(reportTaskId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("report.task.not_found")
                ));
        TaskEntity task = existingReport.getTaskId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        if (!task.getAssignee().getId().equals(user.getId())) {
            throw new AppException(HttpStatus.FORBIDDEN, LocalizationUtils.getMessage("report.task.not_assigned"));
        }

        if (!existingReport.getStartTime().toLocalDate().equals(LocalDate.now())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("report.task.invalid_date"));
        }
        existingReport.setDescription(updatedReportTask.getDescription());
        existingReport.setComment(updatedReportTask.getComment());
        existingReport.setStatus(ReportStatus.processing);
        existingReport.setEndTime(LocalDateTime.now());

        if (images != null && !images.isEmpty()) {
            if (existingReport.getReportImages() != null) {
                existingReport.getReportImages().clear();
            } else {
                existingReport.setReportImages(new ArrayList<>());
            }

            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    ReportTaskImageEntity reportTaskImage = new ReportTaskImageEntity();
                    reportTaskImage.setReportTask(existingReport);
                    reportTaskImage.setUrl(UploadImagesUtils.storeFile(image, ImageContants.REPORT_IMAGE_PATH));
                    existingReport.getReportImages().add(reportTaskImage);
                }
            }
        }

        return reportTaskRepository.save(existingReport);
    }

    @Override
    public ReportTaskEntity joinTask(long taskId) {
        boolean exists = reportTaskRepository.existsByTaskAndDate(taskId, LocalDate.now());
        if (exists) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("report.task.already_exists"));
        }
        ReportTaskEntity reportTask = new ReportTaskEntity();
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Task not found."));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        if (!task.getAssignee().getId().equals(user.getId())) {
            throw new AppException(HttpStatus.FORBIDDEN, LocalizationUtils.getMessage("report.task.not_assigned")
            );
        }
        LocalDate date = LocalDate.now();
        if (date.isBefore(task.getFromDate()) || date.isAfter(task.getToDate())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("report.task.out_of_range")
            );
        }
        reportTask.setDate(date);
        reportTask.setStartTime(LocalDateTime.now());
        reportTask.setTaskId(task);
        reportTask.setStatus(ReportStatus.pending);

        return reportTaskRepository.save(reportTask);
    }


    @Override
    public ReportTaskEntity getReportTaskById(long id) {
        return reportTaskRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("report.task.not_exist")));
    }

    @Override
    public List<ReportTaskEntity> getAllReportTasks() {
        return reportTaskRepository.findAll();
    }

    @Override
    public ReportTaskEntity updateReportTask(
            Long id, ReportTaskUpdateRequest request, List<MultipartFile> newImages)
            throws IOException {
        ReportTaskEntity reportTask = reportTaskRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                LocalizationUtils.getMessage("report.task.not_exist")));

        LocalDate today = LocalDate.now();
        if (!reportTask.getDate().isEqual(today)) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("report.task.update_date_invalid")
            );
        }

        if (request.getDescription() != null) {
            reportTask.setDescription(request.getDescription());
        }

        if (request.getDeleteUrls() != null && !request.getDeleteUrls().isEmpty()) {
            List<ReportTaskImageEntity> imagesToDelete = reportTask.getReportImages().stream()
                    .filter(image -> request.getDeleteUrls().contains(image.getUrl()))
                    .collect(Collectors.toList());

            for (ReportTaskImageEntity image : imagesToDelete) {
                UploadImagesUtils.deleteFile(image.getUrl(), ImageContants.REPORT_IMAGE_PATH);
            }

            reportTaskImageRepository.deleteAll(imagesToDelete);
            reportTask.getReportImages().removeAll(imagesToDelete);
        }

        if (newImages != null && !newImages.isEmpty()) {
            List<ReportTaskImageEntity> newImageEntities = new ArrayList<>();
            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    String imageUrl = UploadImagesUtils.storeFile(image, ImageContants.REPORT_IMAGE_PATH);
                    ReportTaskImageEntity imageEntity = ReportTaskImageEntity.builder()
                            .url(imageUrl)
                            .reportTask(reportTask)
                            .build();
                    newImageEntities.add(imageEntity);
                }
            }
            reportTask.getReportImages().addAll(newImageEntities);
        }

        return reportTaskRepository.save(reportTask);
    }

    @Override
    public void deleteReportTask(long id) {
        ReportTaskEntity warehouseLocation = reportTaskRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Warehouse", "id", id));

        reportTaskRepository.delete(warehouseLocation);
    }

    @Override
    public List<ReportTaskEntity> getReportsByTask(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new DataNotFoundException("Task", "id", taskId));
        return reportTaskRepository.findByTaskId(task);
    }

    @Override
    public ReportTaskEntity getReportsByTaskAndDate(Long taskId, LocalDate date) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new DataNotFoundException("Task", "id", taskId));
        return reportTaskRepository.findByTaskIdAndDate(task, date);
    }

    @Override
    public ReportTaskEntity reviewReportTask(Long id, ReviewReportTaskRequest request){
        ReportTaskEntity reportTask = reportTaskRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("ReportTask", "id", id));

        reportTask.setComment(request.getComment());
        reportTask.setStatus(ReportStatus.closed);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        reportTask.setReviewer_id(user);

        return reportTaskRepository.save(reportTask);
    }

    @Override
    public List<ReportTaskResponse> getReportTasksByDate(LocalDate date) {
        List<ReportTaskEntity> reportTasks = reportTaskRepository.findByDate(date);
        return reportTaskMapper.toResponseList(reportTasks);
    }
}
