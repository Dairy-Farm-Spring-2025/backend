package com.capstone.dfms.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vaccine_cycles")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineCycleEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vaccineCycleId;

    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "cow_type_id")
    private CowTypeEntity cowTypeEntity;

    @OneToMany(mappedBy = "vaccineCycleEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccineCycleDetailEntity> vaccineCycleDetails;
}
