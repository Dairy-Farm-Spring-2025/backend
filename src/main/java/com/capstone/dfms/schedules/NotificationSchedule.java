package com.capstone.dfms.schedules;

import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.CategoryNotification;
import com.capstone.dfms.repositories.IReportTaskRepository;
import com.capstone.dfms.repositories.ITaskRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.NotificationRequest;
import com.capstone.dfms.services.INotificationService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class NotificationSchedule {
    private final INotificationService notificationService;
    private final IUserRepository userRepository;
    private final ITaskRepository taskRepository;
    private final IReportTaskRepository reportTaskRepository;


    @Scheduled(cron = "0 0 7 * * ?")
    public void sendDailyTaskNotification() {
        LocalDate today = LocalDate.now();
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            List<TaskEntity> todayTasks = taskRepository.findTodayTasksByUser(user.getId(), today);
            if (!todayTasks.isEmpty()) {
                NotificationRequest request = new NotificationRequest();
                request.setTitle("Thông báo công việc hôm nay");
                request.setDescription("Bạn có " + todayTasks.size() + " công việc cần làm hôm nay.");
                request.setLink("/tasks");
                request.setCategory(CategoryNotification.task);
                request.setUserIds(Collections.singletonList(user.getId()));
                notificationService.createNotification(request);
            }
        }
    }

    public void sendNotificationToUser(Long userId, String title, String description, String link, CategoryNotification category) {
        NotificationRequest request = new NotificationRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setLink(link);
        request.setCategory(category);
        request.setUserIds(Collections.singletonList(userId));
        notificationService.createNotification(request);
    }


    @Scheduled(cron = "0 30 16 * * ?")
    public void sendPendingReportReminders() {
        LocalDate today = LocalDate.now();
        List<ReportTaskEntity> pendingReports = reportTaskRepository.findPendingReportsForToday(today);

        for (ReportTaskEntity report : pendingReports) {
            UserEntity user = report.getTaskId().getAssignee();
            NotificationRequest request = new NotificationRequest();
            request.setTitle("Nhắc nhở báo cáo công việc");
            request.setDescription("Bạn có công việc chưa hoàn thành báo cáo. Vui lòng hoàn thành trước 17:00.");
            request.setLink("reportTask/"+ report.getReportTaskId());
            request.setCategory(CategoryNotification.task);
            request.setUserIds(Collections.singletonList(user.getId()));

            notificationService.createNotification(request);
        }
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendUnreportedTaskNotifications() {
        LocalDate today = LocalDate.now();
        List<TaskEntity> unreportedTasks = taskRepository.findUnreportedDayShiftTasks(today);

        for (TaskEntity task : unreportedTasks) {
            UserEntity assignee = task.getAssignee();
            if (assignee != null) {
                NotificationRequest request = new NotificationRequest();
                request.setTitle("Nhắc nhở check in công việc");
                request.setDescription("Bạn có công việc chưa check in. Vui lòng kiểm tra!");
                request.setLink("task/"+ task.getTaskId());
                request.setCategory(CategoryNotification.task);
                request.setUserIds(Collections.singletonList(assignee.getId()));
                notificationService.createNotification(request);
            }
        }
    }
}
