package com.capstone.dfms.schedules;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.PriorityTask;
import com.capstone.dfms.models.enums.ReportStatus;
import com.capstone.dfms.models.enums.TaskShift;
import com.capstone.dfms.models.enums.TaskStatus;
import com.capstone.dfms.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskSchedule {
    private final ITaskRepository taskRepository;
    private final IIllnessDetailRepository illnessDetailRepository;
    private final IReportTaskRepository reportTaskRepository;
    private final ITaskTypeRepository taskTypeRepository;
    private final IRoleRepository roleRepository;
    private final ICowPenRepository cowPenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTaskStatus() {
        LocalDate today = LocalDate.now();
        List<TaskEntity> tasks = taskRepository.findAll();

        for (TaskEntity task : tasks) {
            if (task.getFromDate().isEqual(today) && task.getStatus() != TaskStatus.inProgress) {
                task.setStatus(TaskStatus.inProgress);
            }

            if (task.getToDate().plusDays(1).isEqual(today) && task.getStatus() != TaskStatus.completed) {
                task.setStatus(TaskStatus.completed);
            }
        }

        taskRepository.saveAll(tasks);
    }

    @Scheduled(cron = "0 0 17 * * ?")
    public void markMissingReports() {
        LocalDate today = LocalDate.now();
        List<ReportTaskEntity> pendingReports = reportTaskRepository.findPendingReportsForToday(today);

        if (!pendingReports.isEmpty()) {
            for (ReportTaskEntity report : pendingReports) {
                report.setStatus(ReportStatus.misssing);
            }
            reportTaskRepository.saveAll(pendingReports);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createTasksForNextWeek() {
        LocalDate nextMonday = LocalDate.now().plusDays(3);
        LocalDate nextSunday = nextMonday.plusDays(6);

        List<IllnessDetailEntity> illnessDetails = illnessDetailRepository.findByDateBetween(nextMonday, nextSunday);

        for (IllnessDetailEntity illnessDetail : illnessDetails) {
            RoleEntity role = roleRepository.findById(3L).orElseThrow(()
                    -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("user.login.role_not_exist")));

            TaskTypeEntity treatmentTaskType = taskTypeRepository.findByName("Chữa bệnh")
                    .orElseGet(() -> {
                        TaskTypeEntity newTaskType = new TaskTypeEntity();
                        newTaskType.setName("Chữa bệnh");
                        newTaskType.setRoleId(role);
                        newTaskType.setDescription("Công việc điều trị bệnh cho bò");
                        return taskTypeRepository.save(newTaskType);
                    });
            CowEntity cow = illnessDetail.getIllnessEntity().getCowEntity();
            CowPenEntity latestCowPen = cowPenRepository.latestCowPenByCowId(cow.getCowId());

            TaskEntity task = TaskEntity.builder()
                    .description("Điều trị bệnh cho bò")
                    .status(TaskStatus.pending)
                    .fromDate(illnessDetail.getDate())
                    .toDate(illnessDetail.getDate())
                    .areaId(latestCowPen.getPenEntity().getAreaBelongto())
                    .assigner(null)
                    .assignee(null)
                    .taskTypeId(treatmentTaskType)
                    .priority(PriorityTask.high)
                    .shift(TaskShift.dayShift)
                    .illness(illnessDetail)
                    .build();

            taskRepository.save(task);
        }
    }



}
