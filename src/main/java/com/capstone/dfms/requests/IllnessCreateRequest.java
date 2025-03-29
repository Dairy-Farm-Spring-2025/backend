package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.IllnessSeverity;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessCreateRequest {
    private String symptoms;
    private IllnessSeverity severity;
    private String prognosis;
    private Long cowId;

    List<IllnessDetailPlanVet> detail;
}
