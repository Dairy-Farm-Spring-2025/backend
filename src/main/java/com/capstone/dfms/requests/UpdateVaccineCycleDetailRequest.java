package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.InjectionSite;
import com.capstone.dfms.models.enums.ItemUnit;
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

    private double dosage;

    private InjectionSite injectionSite;

    private int ageInMonths;

    private Long itemId;
}
