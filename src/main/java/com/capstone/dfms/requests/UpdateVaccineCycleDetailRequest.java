package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.InjectionSite;
import com.capstone.dfms.models.enums.ItemUnit;
import com.capstone.dfms.models.enums.UnitPeriodic;
import com.capstone.dfms.models.enums.VaccineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVaccineCycleDetailRequest {
    private Long id;

    private String name;

    private String description;

    private ItemUnit dosageUnit;

    private String vaccineIngredients;

    private VaccineType vaccineType;

    private double dosage;

    private InjectionSite injectionSite;

    private UnitPeriodic unitPeriodic;

    private Integer numberPeriodic;

    private Long itemId;

    private Integer firstInjectionMonth;
}
