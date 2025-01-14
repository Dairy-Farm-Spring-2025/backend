package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "report_tasks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportTaskId;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private LocalDate date;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "task_id ")
    private TaskEntity taskId;
}
