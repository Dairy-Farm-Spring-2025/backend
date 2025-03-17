package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.IllnessSeverity;
import com.capstone.dfms.models.enums.IllnessStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "illnesses")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IllnessEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long illnessId;

    private String symptoms;

    @Enumerated(EnumType.STRING)
    private IllnessSeverity severity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String prognosis;

    @Enumerated(EnumType.STRING)
    private IllnessStatus illnessStatus;

    @ManyToOne
    @JoinColumn(name = "cow_id")
    private CowEntity cowEntity;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private UserEntity veterinarian;

    @OneToMany(mappedBy = "illnessEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<IllnessDetailEntity> illnessDetails;
}
