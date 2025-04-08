package com.capstone.dfms.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTaskDBResponse {
    private Long reportTaskId;
    private String description;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
