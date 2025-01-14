package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.InjectionSite;
import com.capstone.dfms.models.enums.ItemUnit;
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
    private double dosage;

    @Enumerated(EnumType.STRING)
    private InjectionSite injectionSite;
    private int ageInMonths;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity itemEntity;

    @ManyToOne
    @JoinColumn(name = "vaccine_cycle_id")
    private VaccineCycleEntity vaccineCycleEntity;


}
