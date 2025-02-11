package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.IllnessSeverity;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessPrognosisRequest {
    private IllnessSeverity severity;
    private String prognosis;
}
