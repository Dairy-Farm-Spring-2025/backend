package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.InjectionSite;
import com.capstone.dfms.models.enums.ItemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineCycleDetailRequest {
    private String name;

    private String description;

    private ItemUnit dosageUnit;

    private double dosage;

    private InjectionSite injectionSite;

    private int ageInMonths;

    private Long itemId;
}
