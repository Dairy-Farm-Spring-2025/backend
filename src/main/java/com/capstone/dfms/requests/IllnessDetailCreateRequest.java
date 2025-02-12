package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.IllnessDetailStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IllnessDetailCreateRequest {
    private LocalDate date;
    private String description;

    @Enumerated(EnumType.STRING)
    private IllnessDetailStatus status;

    private Long veterinarianId;
    private Long itemId;
    private Long illnessId;
}
