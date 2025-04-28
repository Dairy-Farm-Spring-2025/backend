package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.PenCowStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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


}
