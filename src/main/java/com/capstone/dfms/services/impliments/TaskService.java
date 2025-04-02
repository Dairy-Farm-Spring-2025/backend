package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.mappers.IReportTaskMapper;
import com.capstone.dfms.mappers.ITaskMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.TaskShift;
import com.capstone.dfms.models.enums.TaskStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.requests.UpdateTaskRequest;
import com.capstone.dfms.responses.RangeTaskResponse;
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
    private final IIllnessRepository illnessRepository;
    private final ITaskMapper taskMapper;
    private final IReportTaskMapper reportTaskMapper;

    @Override
    public List<TaskEntity> createMultipleTasks(TaskRequest request) {
        List<TaskEntity> tasks = new ArrayList<>();

        AreaEntity area = areaRepository.findById(request.getAreaId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Không tìm thấy khu vực"));

        TaskTypeEntity taskType = taskTypeRepository.findById(request.getTaskTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Không tìm thấy loại công việc"));

        IllnessEntity illness = null;
        if(request.getIllnessId() != null){
            illness = illnessRepository.findById(request.getIllnessId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Không tìm thấy loại bệnh"));
        }

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
            if (!assignee.getRoleId().getName().equalsIgnoreCase("Veterinarians") && illness == null) {
                if (isDuplicateTaskTypeAndArea(assigneeId, request.getTaskTypeId(), request.getAreaId(), request.getFromDate(), request.getToDate())) {
                    throw new AppException(HttpStatus.BAD_REQUEST, "Người dùng " + assignee.getName() + " đã có công việc loại này trong khu vực này trong thời gian này.");
                }
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

            if(illness != null){
                task.setMainIllness(illness);
            }
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
    public Map<LocalDate, List<RangeTaskResponse>> getMyTasksByDateRange2(LocalDate startDate, LocalDate endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getUser().getId();

        List<TaskEntity> taskEntities = taskRepository.findMyTasksInDateRange(userId, startDate, endDate);
        List<ReportTaskEntity> reportTaskEntities = reportTaskRepository.findReportTasksByUserIdAndDateRange(userId, startDate, endDate);
        return mapTasksByDateRange2(taskEntities, reportTaskEntities, startDate, endDate);
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
                        ReportTaskEntity reportTask = reportTaskEntities.stream()
                                .filter(report -> report.getTaskId().getTaskId().equals(task.getTaskId()) &&
                                        report.getDate().isEqual(dateToCheck))
                                .findFirst()
                                .orElse(null);

                        TaskResponse taskResponse = ITaskMapper.INSTANCE.toResponse(task);
                        taskResponse.setReportTask(reportTask);

                        return taskResponse;
                    })
                    .collect(Collectors.toList());

            taskMap.put(currentDate, tasksForDay.isEmpty() ? null : tasksForDay);
            currentDate = currentDate.plusDays(1);
        }

        return taskMap;
    }


    private Map<LocalDate, List<RangeTaskResponse>> mapTasksByDateRange2(
            List<TaskEntity> taskEntities,
            List<ReportTaskEntity> reportTaskEntities,
            LocalDate startDate,
            LocalDate endDate) {

        Map<LocalDate, List<RangeTaskResponse>> taskMap = new LinkedHashMap<>();
        LocalDate currentDate = startDate;

        List<RangeTaskResponse> taskResponses = ITaskMapper.INSTANCE.toResponseList2(taskEntities);

        Map<Long, List<ReportTaskEntity>> reportTaskMap = reportTaskEntities.stream()
                .collect(Collectors.groupingBy(report -> report.getTaskId().getTaskId()));

        while (!currentDate.isAfter(endDate)) {
            final LocalDate dateToCheck = currentDate;

            List<RangeTaskResponse> tasksForDay = taskResponses.stream()
                    .filter(task ->
                            (task.getFromDate().isEqual(dateToCheck) || task.getFromDate().isBefore(dateToCheck)) &&
                                    (task.getToDate().isEqual(dateToCheck) || task.getToDate().isAfter(dateToCheck)))
                    .peek(task -> {
                        List<ReportTaskEntity> reports = reportTaskMap.get(task.getTaskId());
                        if (reports != null) {
                            ReportTaskEntity reportForDay = reports.stream()
                                    .filter(report -> report.getDate().isEqual(dateToCheck))
                                    .findFirst()
                                    .orElse(null);
                            task.setReportTask(reportForDay);
                        }
                    })
                    .collect(Collectors.toList());

            taskMap.put(currentDate, tasksForDay.isEmpty() ? null : tasksForDay);
            currentDate = currentDate.plusDays(1);
        }

        return taskMap;
    }

    @Override
    public TaskEntity updateAssigneeForTask(Long taskId, Long assigneeId){
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId));

        UserEntity assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new DataNotFoundException("User", "id", assigneeId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity assigner = userPrincipal.getUser();
        task.setAssigner(assigner);
        task.setAssignee(assignee);
        return taskRepository.save(task);
    }


    @Override
    public TaskEntity updateTask(Long taskId, UpdateTaskRequest request) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId));

        LocalDate toDate = task.getToDate();
        LocalDate fromDate = task.getFromDate();
        UserEntity assignee = task.getAssignee();

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getFromDate() != null) {
            LocalDate newFromDate = request.getFromDate();

            if (LocalDate.now().isAfter(task.getFromDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Cannot update task as the from date is already in the future.");
            }

            if (newFromDate.isBefore(LocalDate.now())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải từ hôm nay hoặc sau.");
            }

            if (!isAssigneeAvailableForTask(assignee.getId(), newFromDate, toDate)) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        "Người dùng " + assignee.getName() + " đã đạt giới hạn tối đa 6 ngày làm việc trong tuần.");
            }

            task.setFromDate(newFromDate);
        }


        if (request.getToDate() != null) {
            if (!isAssigneeAvailableForTask(assignee.getId(), fromDate, request.getToDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        "Người dùng " + assignee.getName() + " đã đạt giới hạn tối đa 6 ngày làm việc trong tuần.");
            }else{
                task.setToDate(request.getToDate());

            }
        }

        if (request.getAreaId() != null) {
            AreaEntity area = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Area not found with ID: " + request.getAreaId()));
            task.setAreaId(area);
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        List<LocalDate> offDates = request.getOffDates();

        if (offDates != null && !offDates.isEmpty()) {
            for (LocalDate offDate : offDates) {
                if (!offDate.isBefore(task.getFromDate()) && !offDate.isAfter(task.getToDate())) {
                    task.setToDate(offDate.minusDays(1));
                    task.setStatus(TaskStatus.pending);
                    taskRepository.save(task);

                    TaskEntity newTask = new TaskEntity();
                    newTask.setDescription(task.getDescription());
                    newTask.setFromDate(offDate.plusDays(1));
                    newTask.setToDate(toDate);
                    newTask.setShift(TaskShift.dayShift);
                    newTask.setAssignee(assignee);
                    newTask.setAreaId(task.getAreaId());
                    newTask.setPriority(task.getPriority());
                    newTask.setStatus(TaskStatus.pending);
                    taskRepository.save(newTask);

                    return task;
                }
            }
        }
        return taskRepository.save(task);
    }

    @Override
    public RangeTaskResponse getTaskDetail(Long taskId) {
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,"Task not found with id: " + taskId));

        return ITaskMapper.INSTANCE.toResponse2(taskEntity);
    }
}
