package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableUserImportRequest {
    private Long roleId;

    private String areaName;

    private LocalDate fromDate;

    private LocalDate toDate;
}
