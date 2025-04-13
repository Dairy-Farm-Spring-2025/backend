package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.CowTypeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowTypeUpdateRequest {
    private String name;
    private String description;
    private CowTypeStatus status;
    private Long maxWeight;
    private Long maxLength;
    private Long maxHeight;
}
