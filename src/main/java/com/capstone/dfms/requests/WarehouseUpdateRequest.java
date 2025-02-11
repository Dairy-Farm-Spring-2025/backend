package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseUpdateRequest {
    private String name;

    private String description;
}
