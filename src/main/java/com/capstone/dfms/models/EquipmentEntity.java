package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.EquipmentStatus;
import com.capstone.dfms.models.enums.EquipmentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long equipmentId;

    private String name;

    @Enumerated(EnumType.STRING)
    private EquipmentType type;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

    private String description;

    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private WarehouseLocationEntity warehouseLocationEntity;
}
