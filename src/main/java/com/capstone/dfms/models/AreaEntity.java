package com.capstone.dfms.models;

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

    private float length;

    private float width;
}
