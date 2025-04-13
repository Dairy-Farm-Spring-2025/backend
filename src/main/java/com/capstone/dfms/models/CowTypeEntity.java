package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.CowTypeStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cow_types")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowTypeEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cowTypeId;

    private String name;
    private String description;

    private Long maxWeight;

    private Long maxLength;

    private Long maxHeight;

    @Enumerated(EnumType.STRING)
    private CowTypeStatus status;
}
