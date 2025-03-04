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
public class TaskResponse {
    private Long taskId;
    private String description;
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String areaName;
    private String taskTypeName;
    private String assignerName;
    private String assigneeName;
    private String priority;
    private String shift;
    private String completionNotes;
}
