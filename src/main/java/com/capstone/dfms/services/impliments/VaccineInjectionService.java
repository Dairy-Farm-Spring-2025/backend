package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.IVaccineInjectionMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.*;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.VaccineInjectionRequest;
import com.capstone.dfms.services.IVaccineInjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineInjectionService implements IVaccineInjectionService {
    private final IVaccineInjectionRepository vaccineInjectionRepository;
    private final ICowRepository cowRepository;
    private final IVaccineCycleDetailRepository vaccineCycleDetailRepository;
    private final IVaccineInjectionMapper vaccineInjectionMapper;
    private final ITaskTypeRepository taskTypeRepository;
    private final IVaccineCycleRepository vaccineCycleRepository;
    private final ITaskRepository taskRepository;
    private final IRoleRepository roleRepository;
    private final ICowPenRepository cowPenRepository;

    public void createVaccineInjection(){
        this.testCreateVaccineInjection();
    }

    @Override
    public List<VaccineInjectionEntity> getAllVaccineInjections() {
        return vaccineInjectionRepository.findAll();
    }

    @Override
    public VaccineInjectionEntity getVaccineInjectionById(Long id) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        return entity;
    }

    @Override
    public VaccineInjectionEntity updateVaccineInjection(Long id, VaccineInjectionRequest request) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        entity.setInjectionDate(request.getInjectionDate());
        return null;
    }

    @Override
    public void deleteVaccineInjection(Long id) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        vaccineInjectionRepository.delete(entity);
    }

    @Override
    public VaccineInjectionEntity reportVaccineInjection(Long id, InjectionStatus status) {
        VaccineInjectionEntity entity = vaccineInjectionRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("vaccine_injection.not_found")));

        if(entity.getInjectionDate().equals(LocalDate.now())){
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("report.no_time"));
        }

        entity.setAdministeredBy(UserStatic.getCurrentUser());
        entity.setStatus(status);
        return vaccineInjectionRepository.save(entity);
    }

    public void testCreateVaccineInjection() {
        List<VaccineInjectionEntity> newVaccineInjectionEntities = new ArrayList<>();
        List<TaskEntity> newTaskEntities = new ArrayList<>();

        List<VaccineCycleEntity> cycleEntities = vaccineCycleRepository.findAllWithDetails();
        cycleEntities.forEach(cycle -> {
            List<CowEntity> cowEntitiesByCowType =
                    cowRepository.findByCowTypeEntity_CowTypeIdAndDateOfOutIsNullOrDateOfOutAfter
                            (cycle.getCowTypeEntity().getCowTypeId(), LocalDate.now());

            cycle.getVaccineCycleDetails().forEach(details -> {
                cowEntitiesByCowType.forEach(cow -> {
                    if(!cow.getCowStatus().equals(CowStatus.culling)) {
                        List<VaccineInjectionEntity> vaccineInjectionEntities =
                                vaccineInjectionRepository.findVaccineInjectionsByCowAndVaccineCycleDetail(cow.getCowId(),
                                        details.getVaccineCycleDetailId());

                        LocalDate startMonth = LocalDate.now();
                        LocalDate endMonth = LocalDate.now().plusDays(8);

                        LocalDate nextInjectionDate;
                        if (vaccineInjectionEntities.isEmpty()) {
                            nextInjectionDate = cow.getDateOfBirth().plusMonths(details.getFirstInjectionMonth());
                        } else {
                            VaccineInjectionEntity latestInjection = vaccineInjectionEntities.get(0);
                            nextInjectionDate = calculateNextInjectionDate(latestInjection.getInjectionDate(), details);
                        }

                        // 🔹 Generate injections within the next month
                        while (nextInjectionDate.isBefore(endMonth)) {
                            if (nextInjectionDate.isAfter(startMonth)) {
                                VaccineInjectionEntity newInjection = VaccineInjectionEntity.builder()
                                        .injectionDate(nextInjectionDate)
                                        .cowEntity(cow)
                                        .vaccineCycleDetail(details)
                                        .status(InjectionStatus.pending)
                                        .description("Tiêm phòng cho: " + cow.getName() +
                                                " - Vaccine: " + details.getItemEntity().getName())
                                        .build();

                                newVaccineInjectionEntities.add(newInjection);

                                RoleEntity role = roleRepository.findById(3L).orElseThrow(()
                                        -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("user.login.role_not_exist")));

                                TaskTypeEntity injectionTaskType = taskTypeRepository.findByName("Tiêm ngừa")
                                        .orElseGet(() -> {
                                            TaskTypeEntity newTaskType = new TaskTypeEntity();
                                            newTaskType.setName("Tiêm ngừa");
                                            newTaskType.setRoleId(role);
                                            newTaskType.setDescription("Công việc tiêm ngừa cho bò");
                                            return taskTypeRepository.save(newTaskType);
                                        });

                                CowPenEntity latestCowPen = cowPenRepository.latestCowPenByCowId(cow.getCowId());
                                AreaEntity area = null;
                                if (latestCowPen != null && latestCowPen.getPenEntity() != null) {
                                    area = latestCowPen.getPenEntity().getAreaBelongto();
                                }

                                //  Create Task for This Injection
                                TaskEntity newTask = TaskEntity.builder()
                                        .description("Administer vaccine to cow: " + cow.getName())
                                        .status(TaskStatus.pending)
                                        .fromDate(nextInjectionDate)
                                        .toDate(nextInjectionDate)
                                        .priority(PriorityTask.high)
                                        .shift(TaskShift.dayShift)
                                        .taskTypeId(injectionTaskType)
                                        .vaccineInjection(newInjection)
                                        .areaId(area)
                                        .build();

                                newTaskEntities.add(newTask);
                            }

                            // Move to the next scheduled injection date
                            nextInjectionDate = calculateNextInjectionDate(nextInjectionDate, details);
                        }
                    }
                });
            });
        });

        vaccineInjectionRepository.saveAll(newVaccineInjectionEntities);
        taskRepository.saveAll(newTaskEntities); // Save all tasks
    }

    private LocalDate calculateNextInjectionDate(LocalDate lastInjectionDate, VaccineCycleDetailEntity details) {
        return switch (details.getUnitPeriodic()) {
            case days -> lastInjectionDate.plusDays(details.getNumberPeriodic());
            case weeks -> lastInjectionDate.plusWeeks(details.getNumberPeriodic());
            case months -> lastInjectionDate.plusMonths(details.getNumberPeriodic());
            case years -> lastInjectionDate.plusYears(details.getNumberPeriodic());
            default -> throw new IllegalArgumentException("Unexpected periodic unit: " + details.getUnitPeriodic());
        };
    }


}
