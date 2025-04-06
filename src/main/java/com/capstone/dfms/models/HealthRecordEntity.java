package com.capstone.dfms.models;

import com.alibaba.excel.annotation.ExcelProperty;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HealthRecordEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long healthRecordId;

    @Enumerated(EnumType.STRING)
    private HealthRecordStatus status;

    private float size;

    @Enumerated(EnumType.STRING)
    private CowStatus period;

    @ManyToOne
    @JoinColumn(name = "cow_id")
    private CowEntity cowEntity;
//
//    @ManyToOne
//    @JoinColumn(name = "task_id")
//    private TaskEntity relatedTask;

    private LocalDateTime reportTime;
    private String description;
    private float weight;
    private float bodyTemperature;
    private float heartRate;
    private float respiratoryRate;
    private float ruminateActivity;
    private float chestCircumference;
    private float bodyLength;
}
