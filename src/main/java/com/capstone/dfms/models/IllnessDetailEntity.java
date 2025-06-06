package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.IllnessDetailStatus;
import com.capstone.dfms.models.enums.InjectionSite;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "illness_details")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IllnessDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long illnessDetailId;

    private LocalDate date;

    @Column(length = 1000)
    private String description;

    private double dosage;

    @Enumerated(EnumType.STRING)
    private InjectionSite injectionSite;

    @Enumerated(EnumType.STRING)
    private IllnessDetailStatus status;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private UserEntity veterinarian;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private ItemEntity vaccine;

    @ManyToOne
    @JoinColumn(name = "illness_id")
    @JsonBackReference
    private IllnessEntity illnessEntity;
}
