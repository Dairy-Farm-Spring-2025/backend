package com.capstone.dfms.responses;

import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.models.enums.AreaType;
import com.capstone.dfms.models.enums.CowStatus;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private float penLength;
    private float penWidth;
    private Integer numberInRow;
    private Integer maxPen;
    private AreaType areaType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long occupiedPens;  
    private long emptyPens;
    private long damagedPens;
    private CowStatus cowStatus;
    private CowTypeEntity cowTypeEntity;
}
