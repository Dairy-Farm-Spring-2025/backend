package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.mappers.IReportTaskMapper;
import com.capstone.dfms.mappers.ITaskMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.TaskStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.responses.TaskResponse;
import com.capstone.dfms.services.ITaskService;
import com.capstone.dfms.services.ITaskTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService implements ITaskService {
    private final ITaskRepository taskRepository;
    private final IAreaRepository areaRepository;
    private final IUserRepository userRepository;
    private final ITaskTypeRepository taskTypeRepository;
    private final IReportTaskRepository reportTaskRepository;
    private final ITaskMapper taskMapper;
    private final IReportTaskMapper reportTaskMapper;

    @Override
    public List<TaskEntity> createMultipleTasks(TaskRequest request) {
        List<TaskEntity> tasks = new ArrayList<>();

        AreaEntity area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Không tìm thấy khu vực"));

        TaskTypeEntity taskType = taskTypeRepository.findById(request.getTaskTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Không tìm thấy loại công việc"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity assigner = userPrincipal.getUser();

        LocalDate today = LocalDate.now();

        if (request.getFromDate().isBefore(today)) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải từ hôm nay hoặc sau");
        }

        if (request.getToDate().isBefore(request.getFromDate())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau ngày bắt đầu");
        }

        for (Long assigneeId : request.getAssigneeIds()) {
            UserEntity assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new DataNotFoundException("User", "id", assigneeId));

            if (!assignee.getRoleId().equals(taskType.getRoleId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Người được giao không có vai trò phù hợp với loại công việc");
            }
            if (!isAssigneeAvailableForTask(assigneeId, request.getFromDate(), request.getToDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Người dùng " + assignee.getName() + " đã đạt giới hạn tối đa 6 ngày làm việc trong tuần.");
            }
            if (isDuplicateTaskTypeAndArea(assigneeId, request.getTaskTypeId(), request.getAreaId(), request.getFromDate(), request.getToDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Người dùng " + assignee.getName() + " đã có công việc loại này trong khu vực này trong thời gian này.");
            }

            TaskEntity task = TaskEntity.builder()
                    .description(request.getDescription())
                    .status(TaskStatus.pending)
                    .fromDate(request.getFromDate())
                    .toDate(request.getToDate())
                    .areaId(area)
                    .assigner(assigner)
                    .assignee(assignee)
                    .taskTypeId(taskType)
                    .priority(request.getPriority())
                    .shift(request.getShift())
                    .build();
            tasks.add(task);
        }
        return taskRepository.saveAll(tasks);
    }


    @Override
    public TaskEntity getTaskById(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Task is not existed!"));
    }

    @Override
    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void deleteTask(long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Task is not existed!"));
        taskRepository.delete(task);
    }

    private boolean isAssigneeAvailableForTask(Long assigneeId, LocalDate fromDate, LocalDate toDate) {
        LocalDate weekStart = fromDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = fromDate.with(DayOfWeek.SUNDAY);

        List<TaskEntity> tasksInWeek = taskRepository.findByAssigneeAndFromDateBetween(assigneeId, weekStart, weekEnd);

        Set<LocalDate> workDays = new HashSet<>();

        for (TaskEntity task : tasksInWeek) {
            LocalDate current = task.getFromDate();
            while (!current.isAfter(task.getToDate())) {
                workDays.add(current);
                current = current.plusDays(1);
            }
        }

        int daysAlreadyWorked = workDays.size();

        LocalDate current = fromDate;
        while (!current.isAfter(toDate)) {
            workDays.add(current);
            current = current.plusDays(1);
        }

        return workDays.size() <= 6;
    }

    private boolean isDuplicateTaskTypeAndArea(Long assigneeId, Long taskTypeId, Long areaId, LocalDate fromDate, LocalDate toDate) {
        List<TaskEntity> existingTasks = taskRepository.findByAssigneeAndTaskTypeAndAreaAndDateRange(assigneeId, taskTypeId, areaId, fromDate, toDate);
        return !existingTasks.isEmpty();
    }

    @Override
    public Map<LocalDate, List<TaskResponse>> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        List<TaskEntity> taskEntities = taskRepository.findTasksInDateRange(startDate, endDate);
        List<ReportTaskEntity> reportTaskEntities = reportTaskRepository.findByDateRange(startDate, endDate);
        return mapTasksByDateRange(taskEntities, reportTaskEntities, startDate, endDate);
    }

    @Override
    public Map<LocalDate, List<TaskResponse>> getMyTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();

        List<TaskEntity> taskEntities = taskRepository.findMyTasksInDateRange(userId, startDate, endDate);
        List<ReportTaskEntity> reportTaskEntities = reportTaskRepository.findReportTasksByUserIdAndDateRange(userId, startDate, endDate);
        return mapTasksByDateRange(taskEntities, reportTaskEntities, startDate, endDate);
    }



    @Override
    public List<TaskEntity> getMyTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity assignee = userPrincipal.getUser();
        return taskRepository.findMyTasks(assignee.getId());
    }


    @Override
    public TaskEntity getMyTaskById(Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity assignee = userPrincipal.getUser();
        return taskRepository.findMyTaskById(taskId, assignee.getId())
                .orElseThrow(() -> new AppException(HttpStatus.FORBIDDEN,"Bạn không có quyền truy cập task này!"));
    }


    private Map<LocalDate, List<TaskResponse>> mapTasksByDateRange(
            List<TaskEntity> taskEntities,
            List<ReportTaskEntity> reportTaskEntities,
            LocalDate startDate,
            LocalDate endDate) {

        Map<LocalDate, List<TaskResponse>> taskMap = new LinkedHashMap<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            final LocalDate dateToCheck = currentDate;

            List<TaskResponse> tasksForDay = taskEntities.stream()
                    .filter(task ->
                            (task.getFromDate().isEqual(dateToCheck) || task.getFromDate().isBefore(dateToCheck)) &&
                                    (task.getToDate().isEqual(dateToCheck) || task.getToDate().isAfter(dateToCheck)))
                    .map(task -> {
                        // Lọc report task theo task và ngày
                        List<ReportTaskEntity> reportTasks = reportTaskEntities.stream()
                                .filter(reportTask -> reportTask.getTaskId().getTaskId().equals(task.getTaskId()) &&
                                        reportTask.getDate().isEqual(dateToCheck))
                                .collect(Collectors.toList());

                        TaskResponse taskResponse = ITaskMapper.INSTANCE.toResponse(task);
                        taskResponse.setReportTasks(reportTasks); // Set thêm reportTasks

                        return taskResponse;
                    })
                    .collect(Collectors.toList());

            taskMap.put(currentDate, tasksForDay.isEmpty() ? null : tasksForDay);
            currentDate = currentDate.plusDays(1);
        }

        return taskMap;
    }



}
