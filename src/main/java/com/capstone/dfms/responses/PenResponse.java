package com.capstone.dfms.responses;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.models.enums.PenType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenResponse {
    private Long penId;
    private String name;
    private String description;
    private PenType penType;
    private float length;
    private float width;
    private PenStatus penStatus;
    private AreaResponse area;
}
