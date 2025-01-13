package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.AreaType;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaUpdateRequest {
    private String name;
    private String description;
    private float length;
    private float width;
    private AreaType areaType;
}
