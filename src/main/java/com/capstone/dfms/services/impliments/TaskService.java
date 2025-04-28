package com.capstone.dfms.services.impliments;

import com.alibaba.excel.EasyExcel;
import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.exceptions.DataNotFoundException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.IReportTaskMapper;
import com.capstone.dfms.mappers.ITaskMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.CategoryNotification;
import com.capstone.dfms.models.enums.PriorityTask;
import com.capstone.dfms.models.enums.TaskShift;
import com.capstone.dfms.models.enums.TaskStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.CreateTaskExcelRequest;
import com.capstone.dfms.requests.NotificationRequest;
import com.capstone.dfms.requests.TaskRequest;
import com.capstone.dfms.requests.UpdateTaskRequest;
import com.capstone.dfms.responses.RangeTaskResponse;
import com.capstone.dfms.responses.TaskExcelResponse;
import com.capstone.dfms.responses.TaskResponse;
import com.capstone.dfms.services.INotificationService;
import com.capstone.dfms.services.ITaskService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final ICowPenRepository cowPenRepository;
    private final INotificationService notificationService;

    @Override
    public List<TaskEntity> createMultipleTasks(TaskRequest request) {
        List<TaskEntity> tasks = new ArrayList<>();

        TaskTypeEntity taskType = taskTypeRepository.findById(request.getTaskTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("task.task_type_not_found")
                ));

        IllnessEntity illness = null;
        AreaEntity area;

        if (request.getIllnessId() != null) {
            illness = illnessRepository.findById(request.getIllnessId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                            LocalizationUtils.getMessage("illness.not.found")
                    ));

            CowEntity cow = illness.getCowEntity();

            CowPenEntity latestCowPen = cowPenRepository.latestCowPenByCowId(cow.getCowId());
            if (latestCowPen == null || latestCowPen.getPenEntity() == null) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.not.in.pen")
                );

            }

            area = latestCowPen.getPenEntity().getAreaBelongto();
            if (area == null) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.area_not_found")
                );
            }
        } else if (request.getAreaId() != null) {
            area = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.area_not_found")
                    ));
        } else {
            area = null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity assigner = userPrincipal.getUser();

        LocalDate today = LocalDate.now();

        if (request.getFromDate().isBefore(today)) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_start_date")
            );
        }

        if (request.getToDate().isBefore(request.getFromDate())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_end_date")
            );
        }

        for (Long assigneeId : request.getAssigneeIds()) {
            UserEntity assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new DataNotFoundException("User", "id", assigneeId));

            if (!assignee.getRoleId().equals(taskType.getRoleId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_assignee_role=")
                );
            }
            if (!isAssigneeAvailableForTask(assigneeId, request.getFromDate(), request.getToDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        String.format(LocalizationUtils.getMessage("task.overload"), assignee.getName()));
            }
            if (!assignee.getRoleId().getName().equalsIgnoreCase("Veterinarians") && illness == null) {
                if (isDuplicateTaskTypeAndArea(assigneeId, request.getTaskTypeId(), request.getAreaId(), request.getFromDate(), request.getToDate())) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            String.format(LocalizationUtils.getMessage("task.duplicate"), assignee.getName()));
                }
            }
            PriorityTask priority = determinePriority(taskType.getName());

            TaskEntity task = TaskEntity.builder()
                    .description(request.getDescription())
                    .status(TaskStatus.pending)
                    .fromDate(request.getFromDate())
                    .toDate(request.getToDate())
                    .areaId(area)
                    .assigner(assigner)
                    .assignee(assignee)
                    .taskTypeId(taskType)
                    .priority(priority)
                    .shift(request.getShift())
                    .build();

            if(illness != null){
                task.setMainIllness(illness);
                NotificationRequest notificationRequest = new NotificationRequest();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = request.getFromDate().format(formatter);
                notificationRequest.setTitle("Công việc chữa khám bệnh khẩn cấp cho bò " + formattedDate);
                notificationRequest.setDescription("Bạn có một công việc liên quan đến điều trị bệnh cần thực hiện vào ngày " + formattedDate + ".");
                notificationRequest.setLink("/tasks");
                notificationRequest.setCategory(CategoryNotification.task);
                notificationRequest.setUserIds(Collections.singletonList(assignee.getId()));

                notificationService.createNotification(notificationRequest);
            }
            tasks.add(task);
        }
        return taskRepository.saveAll(tasks);
    }


    @Override
    public TaskEntity getTaskById(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.not_found")));
    }

    @Override
    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void deleteTask(long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.not_found")));
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
                .orElseThrow(() -> new AppException(HttpStatus.FORBIDDEN, LocalizationUtils.getMessage("task.access.denied")));
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

        Map<Long, List<ReportTaskEntity>> reportTaskMap = reportTaskEntities.stream()
                .collect(Collectors.groupingBy(report -> report.getTaskId().getTaskId()));

        while (!currentDate.isAfter(endDate)) {
            final LocalDate dateToCheck = currentDate;

            List<RangeTaskResponse> tasksForDay = taskEntities.stream()
                    .filter(task ->
                            (task.getFromDate().isEqual(dateToCheck) || task.getFromDate().isBefore(dateToCheck)) &&
                                    (task.getToDate().isEqual(dateToCheck) || task.getToDate().isAfter(dateToCheck)))
                    .map(task -> {
                        RangeTaskResponse taskResponse = ITaskMapper.INSTANCE.toResponse2(task);
                        List<ReportTaskEntity> reports = reportTaskMap.get(task.getTaskId());
                        if (reports != null) {
                            ReportTaskEntity reportForDay = reports.stream()
                                    .filter(report -> report.getDate().isEqual(dateToCheck))
                                    .findFirst()
                                    .orElse(null);
                            taskResponse.setReportTask(reportForDay);
                        }
                        return taskResponse;
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
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("task.not_found")));

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
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("task.not_found")
                ));

        LocalDate toDate = task.getToDate();
        LocalDate fromDate = task.getFromDate();
        UserEntity assignee = task.getAssignee();

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getFromDate() != null) {
            LocalDate newFromDate = request.getFromDate();

            if (LocalDate.now().isAfter(task.getFromDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_from_date_update"));
            }

            if (newFromDate.isBefore(LocalDate.now())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_start_date"));
            }

            if (!isAssigneeAvailableForTask(assignee.getId(), newFromDate, toDate)) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        String.format(LocalizationUtils.getMessage("task.overload"), assignee.getName()));
            }

            task.setFromDate(newFromDate);
        }


        if (request.getToDate() != null) {
            if (!isAssigneeAvailableForTask(assignee.getId(), fromDate, request.getToDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        String.format(LocalizationUtils.getMessage("task.overload"), assignee.getName()));
            }else{
                task.setToDate(request.getToDate());

            }
        }

        if (request.getAreaId() != null) {
            AreaEntity area = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("task.area_not_found")));
            task.setAreaId(area);
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        List<LocalDate> offDates = request.getOffDates();
        if (offDates != null && !offDates.isEmpty()) {

            Collections.sort(offDates);

            LocalDate firstOffDate = offDates.get(0);
            LocalDate lastOffDate = offDates.get(offDates.size() - 1);

            for (LocalDate offDate : offDates) {
                if (offDate.isBefore(fromDate) || offDate.isAfter(toDate)) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            String.format("%s %s %s %s - %s",
                                    LocalizationUtils.getMessage("task.invalid_off_date_range"),
                                    offDate, fromDate, toDate));
                }
            }

            for (int i = 1; i < offDates.size(); i++) {
                if (!offDates.get(i).equals(offDates.get(i - 1).plusDays(1))) {
                    throw new AppException(HttpStatus.BAD_REQUEST,
                            LocalizationUtils.getMessage("task.off_dates_must_be_consecutive"));
                }
            }

            task.setToDate(firstOffDate.minusDays(1));
            task.setStatus(TaskStatus.pending);
            taskRepository.save(task);

            TaskEntity newTask = new TaskEntity();
            newTask.setTaskTypeId(task.getTaskTypeId());
            newTask.setDescription(task.getDescription());
            newTask.setFromDate(lastOffDate.plusDays(1));
            newTask.setToDate(toDate);
            newTask.setShift(TaskShift.dayShift);
            newTask.setAssignee(assignee);
            newTask.setAreaId(task.getAreaId());
            newTask.setPriority(task.getPriority());
            newTask.setStatus(TaskStatus.pending);
            taskRepository.save(newTask);


            TaskEntity offTask = new TaskEntity();
            offTask.setTaskTypeId(task.getTaskTypeId());
            offTask.setDescription(task.getDescription());
            offTask.setFromDate(firstOffDate);
            offTask.setToDate(lastOffDate);
            offTask.setShift(TaskShift.dayShift);
            offTask.setAssignee(null);
            offTask.setAreaId(task.getAreaId());
            offTask.setPriority(task.getPriority());
            offTask.setStatus(TaskStatus.pending);
            taskRepository.save(offTask);

            return task;
        }

        return taskRepository.save(task);
    }

    @Override
    public RangeTaskResponse getTaskDetail(Long taskId) {
        TaskEntity taskEntity = taskRepository.findById(taskId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("task.not_found")));

        return ITaskMapper.INSTANCE.toResponse2(taskEntity);
    }


    @Override
    public byte[] fillTemplateWithDropdown() throws IOException {
        List<AreaEntity> areaEntities = areaRepository.findAll();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/document/Template Task Dairy Farm.xlsx");
        if (inputStream == null) {
            throw new FileNotFoundException("Template file not found!");
        }
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        String[] areaNames = areaEntities.stream().map(AreaEntity::getName).toArray(String[]::new);

        // Tạo vùng dữ liệu cho danh sách chọn (dropdown)
        CellRangeAddressList addressList = new CellRangeAddressList(1, 100, 1, 1);
        // Tạo DataValidation cho dropdown
        DataValidation validation = createDropdownValidation((XSSFSheet) sheet, areaNames);

        // Áp dụng DataValidation cho cột B
        sheet.addValidationData(validation);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    private DataValidation createDropdownValidation(XSSFSheet sheet, String[] options) {
        String optionsString = String.join(",", options);
        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        DataValidationConstraint validationConstraint = validationHelper.createExplicitListConstraint(optionsString.split(","));

        return validationHelper.createValidation(validationConstraint, new CellRangeAddressList(1, 100, 1, 1)); // B2 đến B100
    }


    @Override
    public Map<String, Map<String, List<TaskExcelResponse>>> importAndGroupTasks(MultipartFile file) {
        List<TaskExcelResponse> allTasks = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            List<TaskExcelResponse> tasks = EasyExcel.read(inputStream)
                    .head(TaskExcelResponse.class)
                    .sheet()
                    .doReadSync();

            for (TaskExcelResponse task : tasks) {
                List<String> errors = new ArrayList<>();

                if (task.getTaskType() == null || task.getTaskType().isBlank()) {
                    errors.add("Loại công việc là bắt buộc");
                }

                if (task.getArea() == null || task.getArea().isBlank()) {
                    errors.add("Khu vực là bắt buộc");
                }

                if (task.getFromDate() == null || task.getFromDate().isBlank()) {
                    errors.add("Ngày bắt đầu là bắt buộc");
                } else if (!isValidDate(task.getFromDate())) {
                    errors.add("Ngày bắt đầu phải có định dạng yyyy-MM-dd");
                } else if (!isFromDateAfterToday(task.getFromDate())) {
                    errors.add("Ngày bắt đầu phải sau ngày hôm nay");
                }

                if (task.getToDate() == null || task.getToDate().isBlank()) {
                    errors.add("Ngày kết thúc là bắt buộc");
                } else if (!isValidDate(task.getToDate())) {
                    errors.add("Ngày kết thúc phải có định dạng yyyy-MM-dd");
                } else if (!isToDateAfterFromDate(task.getFromDate(), task.getToDate())) {
                    errors.add("Ngày kết thúc phải bằng hoặc sau ngày bắt đầu");
                }

                if (!errors.isEmpty()) {
                    task.setError(true);
                    task.setErrorMessage(String.join("; ", errors));
                }

                allTasks.add(task);
            }

            return allTasks.stream()
                    .collect(Collectors.groupingBy(
                            task -> task.getArea() == null || task.getArea().isBlank()
                                    ? "Chưa rõ khu vực"
                                    : task.getArea(),
                            Collectors.groupingBy(
                                    task -> task.getTaskType() == null || task.getTaskType().isBlank()
                                            ? "Chưa rõ loại công việc"
                                            : task.getTaskType()
                            )
                    ));

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage(), e);
        }
    }



    private boolean isFromDateAfterToday(String fromDate) {
        LocalDate today = LocalDate.now();
        LocalDate fromDateLocal = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return fromDateLocal.isAfter(today);
    }

    private boolean isToDateAfterFromDate(String fromDate, String toDate) {
        LocalDate fromDateLocal = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate toDateLocal = LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return !toDateLocal.isBefore(fromDateLocal);
    }

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    @Override
    public List<TaskEntity> createTasksFromExcel(List<CreateTaskExcelRequest> requests) {
        List<TaskEntity> createdTasks = new ArrayList<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity assigner = userPrincipal.getUser();

        for (CreateTaskExcelRequest request : requests) {
            LocalDate today = LocalDate.now();
            if (request.getFromDate().isBefore(today)) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_start_date")
                );
            }

            if (request.getToDate().isBefore(request.getFromDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_end_date"));
            }
            UserEntity assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.assignee_not_found")));


            AreaEntity area = areaRepository.findByName(request.getAreaName());
            if (area == null) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.area_not_found") + request.getAreaName());
            }

            TaskTypeEntity taskTypeEntity = taskTypeRepository.findByName(request.getTaskType())
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.task_type_not_found") + request.getTaskType()));

            PriorityTask priority = determinePriority(request.getTaskType());

            if (!assignee.getRoleId().equals(taskTypeEntity.getRoleId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("task.invalid_assignee_role"));
            }
            if (!isAssigneeAvailableForTask(assignee.getId(), request.getFromDate(), request.getToDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        String.format(LocalizationUtils.getMessage("task.overload"), assignee.getName()));
            }
            if (isDuplicateTaskTypeAndArea(assignee.getId(), taskTypeEntity.getTaskTypeId(),area.getAreaId(), request.getFromDate(), request.getToDate())) {
                throw new AppException(HttpStatus.BAD_REQUEST,
                        String.format(LocalizationUtils.getMessage("task.duplicate"), assignee.getName()));
            }

            TaskEntity task = TaskEntity.builder()
                    .description(request.getDescription())
                    .status(TaskStatus.pending)
                    .fromDate(request.getFromDate())
                    .toDate(request.getToDate())
                    .areaId(area)
                    .assigner(assigner)
                    .assignee(assignee)
                    .taskTypeId(taskTypeEntity)
                    .priority(priority)
                    .shift(request.getShift())
                    .build();

            createdTasks.add(task);
        }

        return taskRepository.saveAll(createdTasks);
    }

    private PriorityTask determinePriority(String taskTypeName) {
        switch (taskTypeName) {
            case "Khám định kì":
            case "Lấy sữa bò":
            case "Cho bò ăn":
                return PriorityTask.medium;
            case "Khám bệnh":
                return PriorityTask.critical;
            default:
                return PriorityTask.low;
        }
    }
}
