package com.capstone.dfms.responses;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.PenCowStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowPenResponse {
    private PenEntity penEntity;
    private CowEntity cowEntity;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private PenCowStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}
