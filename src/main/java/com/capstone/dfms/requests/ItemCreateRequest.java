package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemCreateRequest {
    private String name;

    private String status;

    private String unit;

    private float quantity;

    private Long categoryId;

    private Long locationId;
}
