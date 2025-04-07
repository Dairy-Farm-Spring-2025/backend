package com.capstone.dfms.responses;

import com.capstone.dfms.models.enums.CowOrigin;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowResponse {
    private Long cowId;
    private String name;
    private CowStatus cowStatus;
    private LocalDate dateOfBirth;
    private LocalDate dateOfEnter;
    private LocalDate dateOfOut;
    private String description;
    private CowOrigin cowOrigin;
    private Gender gender;
    private CowTypeResponse cowType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isInPen;
    private PenResponse penResponse;
    private List<CowHealthInfoResponse<?>> healthInfoResponses;
}
