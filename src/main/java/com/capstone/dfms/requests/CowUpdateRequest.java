package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.CowOrigin;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowUpdateRequest {
    private CowStatus cowStatus;
    private LocalDate dateOfBirth;
    private LocalDate dateOfEnter;
    private LocalDate dateOfOut;
    private String description;
    private CowOrigin cowOrigin;
    private Gender gender;
    private Long cowTypeId;
}
