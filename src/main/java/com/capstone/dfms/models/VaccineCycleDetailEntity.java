package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.InjectionSite;
import com.capstone.dfms.models.enums.ItemUnit;
import com.capstone.dfms.models.enums.UnitPeriodic;
import com.capstone.dfms.models.enums.VaccineType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vaccine_cycle_details")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineCycleDetailEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vaccineCycleDetailId;

    private String name;

    private String description;

    private ItemUnit dosageUnit;

    private String vaccineIngredients;

    private VaccineType vaccineType;

    private double dosage;

    @Enumerated(EnumType.STRING)
    private InjectionSite injectionSite;

    private Integer firstInjectionMonth;

    //2 field to relate the time to inject
    private Integer numberPeriodic;

    @Enumerated(EnumType.STRING)
    private UnitPeriodic unitPeriodic;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity itemEntity;

    @ManyToOne
    @JoinColumn(name = "vaccine_cycle_id")
//    @JsonIgnoreProperties("vaccineCycleDetails")
    @JsonIgnore
    private VaccineCycleEntity vaccineCycleEntity;
}
