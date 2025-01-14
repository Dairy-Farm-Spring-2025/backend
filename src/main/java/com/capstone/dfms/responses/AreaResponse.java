package com.capstone.dfms.responses;

import com.capstone.dfms.models.enums.AreaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaResponse {
    private Long areaId;
    private String name;
    private String description;
    private float length;
    private float width;
    private AreaType areaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
