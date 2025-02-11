package com.capstone.dfms.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CowPenApproveRequest {
    @NotNull(message = "Pen ID cannot be null")
    private Long penId;

    @NotNull(message = "Cow ID cannot be null")
    private Long cowId;

    @NotNull(message = "From date cannot be null")
    private LocalDate fromDate;

    @NotNull(message = "You must approve or reject")
    private boolean approval;

}
