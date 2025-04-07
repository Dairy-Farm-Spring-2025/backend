package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ItemStatus;
import com.capstone.dfms.models.enums.ItemUnit;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Enumerated(EnumType.STRING)
    private ItemUnit unit;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private WarehouseLocationEntity warehouseLocationEntity;
}
