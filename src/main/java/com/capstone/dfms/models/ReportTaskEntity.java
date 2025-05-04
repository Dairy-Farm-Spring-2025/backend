package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private LocalDate date;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private UserEntity reviewer_id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "task_id ")
    private TaskEntity taskId;

    @OneToMany(mappedBy = "reportTask", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReportTaskImageEntity> reportImages;
}
