package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.InjectionSite;
import com.capstone.dfms.models.enums.ItemUnit;
import com.capstone.dfms.models.enums.UnitPeriodic;
import com.capstone.dfms.models.enums.VaccineType;
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

    private String vaccineIngredients;

    private VaccineType vaccineType;

    private double dosage;

    private InjectionSite injectionSite;

    private Long itemId;

    private Integer numberPeriodic;

    private UnitPeriodic unitPeriodic;

    private Integer firstInjectionMonth;
}
