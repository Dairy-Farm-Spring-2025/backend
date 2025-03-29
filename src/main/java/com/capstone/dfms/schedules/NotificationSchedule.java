package com.capstone.dfms.schedules;

import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.CategoryNotification;
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
}
