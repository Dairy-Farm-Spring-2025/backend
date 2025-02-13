package com.capstone.dfms.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CowHealthInfoResponse<T>{
    private Long id;         // ID of the record (health record or illness)
    private String type;     // "HEALTH_RECORD" or "ILLNESS"
    private LocalDate date;  // For health records, reportTime as LocalDate; for illnesses, startDate
    private T health;
}
