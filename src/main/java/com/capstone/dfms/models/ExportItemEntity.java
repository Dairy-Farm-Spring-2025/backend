package com.capstone.dfms.models;

import com.capstone.dfms.models.enums.ExportItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "export_items")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportItemEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exportItemId;

    @ManyToOne
    @JoinColumn(name = "picker_id")
    private UserEntity picker;

    private float quantity;

    @ManyToOne
    private ItemBatchEntity itemBatchEntity;

    private LocalDateTime exportDate;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;


    @Enumerated(EnumType.STRING)
    private ExportItemStatus status;
}
