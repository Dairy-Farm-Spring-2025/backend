package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.PenCowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowPenCreateRequest {
    private Long penId;
    private Long cowId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private PenCowStatus status;
}
