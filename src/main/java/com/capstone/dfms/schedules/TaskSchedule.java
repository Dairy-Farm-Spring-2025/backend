package com.capstone.dfms.schedules;

import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.models.enums.TaskStatus;
import com.capstone.dfms.repositories.ITaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskSchedule {
    private final ITaskRepository taskRepository;

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

}
