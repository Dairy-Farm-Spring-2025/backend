package com.capstone.dfms.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_task_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTaskImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportTaskImageId;

    private String url;

    @ManyToOne
    @JoinColumn(name = "report_id ")
    private ReportTaskEntity reportTask;
}
