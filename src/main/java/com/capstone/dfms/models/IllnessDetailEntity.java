package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.IllnessDetailStatus;
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
    private String description;

    @Enumerated(EnumType.STRING)
    private IllnessDetailStatus status;
}
