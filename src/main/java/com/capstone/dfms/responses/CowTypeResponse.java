package com.capstone.dfms.responses;

import com.capstone.dfms.models.enums.CowTypeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowTypeResponse {
    private Long cowTypeId;
    private String name;
    private String description;
    private CowTypeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
