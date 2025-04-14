package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UseEquipmentUpdateRequest {
    private Long requiredQuantity;
    private String note;
}
