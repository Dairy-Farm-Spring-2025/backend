package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "schedule_reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleReportId;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ScheduleEntity scheduleId;
}
