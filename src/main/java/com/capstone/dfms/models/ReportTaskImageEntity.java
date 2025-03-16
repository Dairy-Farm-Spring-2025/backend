package com.capstone.dfms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "report_id ")
    private ReportTaskEntity reportTask;
}
