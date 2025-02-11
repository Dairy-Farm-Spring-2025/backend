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
    private Float length;
    private Float width;
    private Float penLength;
    private Float penWidth;
    private AreaType areaType;
}
