package com.capstone.dfms.responses;

import com.capstone.dfms.models.enums.PenStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PenResponse {
    private Long penId;
    private String name;
    private String description;
    private PenStatus penStatus;
    private AreaResponse area;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
