package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.PenStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenUpdateRequest {
    private String name;
    private String description;

    private PenStatus penStatus;
    private Long areaId;
}
