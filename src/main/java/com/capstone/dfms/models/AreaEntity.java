package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.AreaType;
import com.capstone.dfms.models.enums.CowStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "areas")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaEntity extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long areaId;

    private String name;

    @Column(length = 1000)
    private String description;

    private Float length;

    private Float width;

    private Float penLength;

    private Float penWidth;

    private Integer maxPen;

    private Integer numberInRow;

    @Enumerated(EnumType.STRING)
    private AreaType areaType;

    @Enumerated(EnumType.STRING)
    private CowStatus cowStatus;

    @ManyToOne
    @JoinColumn(name = "cow_type_id")
    private CowTypeEntity cowTypeEntity;
}
