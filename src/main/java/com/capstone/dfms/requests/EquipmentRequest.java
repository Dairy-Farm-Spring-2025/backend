package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.EquipmentStatus;
import com.capstone.dfms.models.enums.EquipmentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentRequest {
    private String name;
    private EquipmentType type;
    private EquipmentStatus status;
    private String description;
    private Long quantity;
}
