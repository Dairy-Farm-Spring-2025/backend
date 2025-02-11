package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.BatchStatus;
import com.capstone.dfms.models.enums.ItemUnit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "item_batches")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemBatchEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemBatchId;

    private float quantity;

    private LocalDate importDate;
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity itemEntity;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplierEntity;
}
