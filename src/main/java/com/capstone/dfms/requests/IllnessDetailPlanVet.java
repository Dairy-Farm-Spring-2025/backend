package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.InjectionSite;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessDetailPlanVet {
    private double dosage;
    private InjectionSite injectionSite;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String description;
    private Long vaccineId;
}
