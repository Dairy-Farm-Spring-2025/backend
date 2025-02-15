package com.capstone.dfms.requests;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthReportRequest {
    private HealthRecordStatus status;
    private float weight;
    private float size;
    private CowStatus period;
    private Long cowId;
//    private LocalDateTime reportTime;
}
