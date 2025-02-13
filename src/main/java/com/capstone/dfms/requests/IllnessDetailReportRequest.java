package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.IllnessDetailStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessDetailReportRequest {
    private String description;
    @Enumerated(EnumType.STRING)
    private IllnessDetailStatus status;
}
