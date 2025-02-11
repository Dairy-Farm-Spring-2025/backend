package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.models.enums.PenType;
import jakarta.validation.constraints.Positive;
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
    private PenType penType;
//    private float length;
//    private float width;
    private PenStatus penStatus;
    private Long areaId;
}
