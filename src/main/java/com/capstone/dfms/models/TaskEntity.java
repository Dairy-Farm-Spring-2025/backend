package com.capstone.dfms.models;

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

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private AreaEntity areaId;

    @ManyToOne
    @JoinColumn(name = "assigner_id")
    private UserEntity assigner;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private UserEntity assignee;

}
