package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.CowOrigin;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowCreateRequest {
//    @NotNull(message = "Name cannot be null")
//    private String name;

    @NotNull(message = "Cow Status cannot be null")
    private CowStatus cowStatus;

    @NotNull(message = "Date of Birth cannot be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Date of Enter cannot be null")
    private LocalDate dateOfEnter;

    @NotNull(message = "Cow Origin cannot be null")
    private CowOrigin cowOrigin;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;

    @Positive(message = "Cow Type ID must be a positive number")
    private Long cowTypeId;

    private String description;
}
