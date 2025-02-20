package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.WarehouseType;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseUpdateRequest {
    private String name;

    private String description;

    private WarehouseType type;

}
