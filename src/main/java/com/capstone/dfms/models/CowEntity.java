package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.CowOrigin;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "cows")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cowId;
    private String name;
    private CowStatus cowStatus;
    private LocalDate dateOfBirth;
    private LocalDate dateOfEnter;
    private LocalDate dateOfOut;
    private String description;
    private CowOrigin cowOrigin;
    private Gender gender;

    @ManyToOne
    @JoinColumn(name = "cow_type_id")
    private CowTypeEntity cowTypeEntity;
}
