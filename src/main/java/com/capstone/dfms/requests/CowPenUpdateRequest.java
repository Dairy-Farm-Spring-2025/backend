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
public class CowPenUpdateRequest {
    private LocalDate toDate;
    private PenCowStatus status;
}
