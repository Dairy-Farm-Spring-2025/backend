package com.capstone.dfms.requests;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.enums.IllnessSeverity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessReportRequest {
    private String symptoms;
    private Long cowId;
}
