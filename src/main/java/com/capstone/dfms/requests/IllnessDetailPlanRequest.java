package com.capstone.dfms.requests;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class IllnessDetailPlanRequest {
    private LocalDate date;
    private String description;
    private Long itemId;
    private Long illnessId;
}
