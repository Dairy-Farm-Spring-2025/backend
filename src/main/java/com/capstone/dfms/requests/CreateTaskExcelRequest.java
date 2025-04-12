package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.TaskShift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskExcelRequest {
    private String areaName;
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
    private TaskShift shift;
    private Long assigneeId;
    private String taskType;
}
