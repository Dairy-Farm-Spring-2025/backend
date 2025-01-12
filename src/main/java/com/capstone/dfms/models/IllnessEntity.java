package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.IllnessSeverity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private IllnessSeverity severity;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "cow_id")
    private CowEntity cowEntity;
}
