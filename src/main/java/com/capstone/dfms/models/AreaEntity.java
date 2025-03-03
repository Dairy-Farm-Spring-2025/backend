package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.AreaType;
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

    private String description;

    private Float length;

    private Float width;

    private Float penLength;

    private Float penWidth;

    private Integer maxPen;

    private Integer numberInRow;

    @Enumerated(EnumType.STRING)
    private AreaType areaType;
}
