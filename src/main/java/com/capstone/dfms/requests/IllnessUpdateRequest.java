package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.IllnessSeverity;
import lombok.*;

import java.time.LocalDate;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessUpdateRequest {
    private String symptoms;
    private IllnessSeverity severity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String prognosis;
    private Long cowId;
}
