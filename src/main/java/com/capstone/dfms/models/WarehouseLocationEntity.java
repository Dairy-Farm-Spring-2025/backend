package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.WarehouseType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_location")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseLocationEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warehouseLocationId;

    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private WarehouseType type;
}
