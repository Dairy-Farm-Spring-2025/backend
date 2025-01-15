package com.capstone.dfms.responses;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.CowPenStatus;
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
    private LocalDate fromDate;
    private LocalDate toDate;
    private CowPenStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
