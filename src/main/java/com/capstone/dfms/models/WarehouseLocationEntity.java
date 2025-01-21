package com.capstone.dfms.models;

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

    private String description;
}
