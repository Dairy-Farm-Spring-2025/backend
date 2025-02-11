package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.PenCowStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Pen ID cannot be null")
    private Long penId;

    @NotNull(message = "Cow ID cannot be null")
    private Long cowId;

    @NotNull(message = "From date cannot be null")
    @Future(message = "From date must be in the future")
    private LocalDate fromDate;
}
