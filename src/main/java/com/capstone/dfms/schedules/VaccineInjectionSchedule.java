package com.capstone.dfms.schedules;

import com.capstone.dfms.mappers.IVaccineInjectionMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.PriorityTask;
import com.capstone.dfms.models.enums.TaskShift;
import com.capstone.dfms.models.enums.TaskStatus;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.services.IVaccineInjectionService;
import com.capstone.dfms.services.impliments.VaccineInjectionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class VaccineInjectionSchedule {
    private final IVaccineInjectionRepository vaccineInjectionRepository;
    private final ICowRepository cowRepository;
    private final IVaccineCycleDetailRepository vaccineCycleDetailRepository;
    private final IVaccineInjectionMapper vaccineInjectionMapper;

    private final IVaccineCycleRepository vaccineCycleRepository;
    private final ITaskRepository taskRepository;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void scheduleVaccineInjectionCreation() {
        this.testCreateVaccineInjection();
    }

    @Transactional
    public void testCreateVaccineInjection() {
        List<VaccineInjectionEntity> newVaccineInjectionEntities = new ArrayList<>();
        List<TaskEntity> newTaskEntities = new ArrayList<>();

        List<VaccineCycleEntity> cycleEntities = vaccineCycleRepository.findAllWithDetails();
        cycleEntities.forEach(cycle -> {
            List<CowEntity> cowEntitiesByCowType = cowRepository.findByCowTypeEntity_CowTypeId(cycle.getCowTypeEntity().getCowTypeId());

            cycle.getVaccineCycleDetails().forEach(details -> {
                cowEntitiesByCowType.forEach(cow -> {
                    List<VaccineInjectionEntity> vaccineInjectionEntities =
                            vaccineInjectionRepository.findVaccineInjectionsByCowAndVaccineCycleDetail(cow.getCowId(),
                                    details.getVaccineCycleDetailId());

                    LocalDate startMonth = LocalDate.now().minusDays(1);
                    LocalDate endMonth = LocalDate.now().plusMonths(1).plusDays(1);

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
                                    .build();

                            newVaccineInjectionEntities.add(newInjection);

                            //  Create Task for This Injection
                            TaskEntity newTask = TaskEntity.builder()
                                    .description("Administer vaccine to cow: " + cow.getName())
                                    .status(TaskStatus.pending)
                                    .fromDate(nextInjectionDate)
                                    .toDate(nextInjectionDate)
                                    .priority(PriorityTask.high)
                                    .shift(TaskShift.dayShift)
                                    .vaccineInjection(newInjection)
                                    .build();

                            newTaskEntities.add(newTask);
                        }

                        // Move to the next scheduled injection date
                        nextInjectionDate = calculateNextInjectionDate(nextInjectionDate, details);
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
