package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.IllnessDetailStatus;
import com.capstone.dfms.models.enums.InjectionSite;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessDetailUpdateRequest {
    private LocalDate date;
    private String description;

    private double dosage;
    private InjectionSite injectionSite;

    @Enumerated(EnumType.STRING)
    private IllnessDetailStatus status;

    private Long itemId;
}
