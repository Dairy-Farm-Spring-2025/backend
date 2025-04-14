package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UseEquipmentEntityRequest {
    private long equipmentId;
    private long taskTypeId;
    private Long requiredQuantity;
    private String note;
}
