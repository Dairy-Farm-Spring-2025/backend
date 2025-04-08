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
public class ApplicationDBResponse {
    private Long applicationId;
    private String title;
    private String content;
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String requestByName;
    private String typeName;
}
