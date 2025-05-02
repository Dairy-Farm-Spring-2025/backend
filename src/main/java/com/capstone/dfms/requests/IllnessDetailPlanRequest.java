package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.InjectionSite;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class IllnessDetailPlanRequest {
    private double dosage;
    private InjectionSite injectionSite;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String description;
    private Long itemId;
    private Long illnessId;
}
