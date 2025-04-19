package com.capstone.dfms.models;

import com.capstone.dfms.models.compositeKeys.UseEquipmentPK;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "use_equipments")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UseEquipmentEntity {
    @EmbeddedId
    private UseEquipmentPK id;

    @ManyToOne
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private EquipmentEntity equipment;

    @ManyToOne
    @MapsId("taskTypeId")
    @JoinColumn(name = "task_type_id")
    @JsonManagedReference
    private TaskTypeEntity taskType;

    private Long requiredQuantity;

    private String note;

}
