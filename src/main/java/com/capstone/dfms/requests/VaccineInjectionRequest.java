package com.capstone.dfms.requests;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VaccineInjectionRequest {
    private Long cowId;
    private Long vaccineCycleDetailId;
    private LocalDate injectionDate;
}
