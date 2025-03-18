package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.PriorityTask;
import com.capstone.dfms.models.enums.TaskShift;
import com.capstone.dfms.models.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDate fromDate;

    private LocalDate toDate;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private AreaEntity areaId;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskTypeEntity taskTypeId;

    @ManyToOne
    @JoinColumn(name = "assigner_id")
    private UserEntity assigner;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private UserEntity assignee;

    @Enumerated(EnumType.STRING)
    private PriorityTask priority;

    @Enumerated(EnumType.STRING)
    private TaskShift shift;

    private String completionNotes;

    @ManyToOne
    @JoinColumn(name = "illness_detail_id")
    private IllnessDetailEntity illness;

    @ManyToOne
    @JoinColumn(name = "vaccine_injection_id")
    private VaccineInjectionEntity vaccineInjection;
}
