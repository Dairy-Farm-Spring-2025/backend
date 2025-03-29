package com.capstone.dfms.models;


import com.capstone.dfms.models.enums.InjectionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "vaccine_injections")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineInjectionEntity  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cow_id")
    private CowEntity cowEntity;

    @ManyToOne
    @JoinColumn(name = "vaccine_cycle_detail_id")
    private VaccineCycleDetailEntity vaccineCycleDetail;

    private LocalDate injectionDate;

    @ManyToOne
    @JoinColumn(name = "vet_id")
    private UserEntity administeredBy;

    @Enumerated(EnumType.STRING)
    private InjectionStatus status;

    private String description;
}
