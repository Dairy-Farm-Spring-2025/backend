package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Month;
import java.time.YearMonth;

@Entity
@Table(name = "user_performance_reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPerformanceReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPerformanceReportId;

    private String title;

    private String description;

    private YearMonth month;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private UserEntity reporter;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
