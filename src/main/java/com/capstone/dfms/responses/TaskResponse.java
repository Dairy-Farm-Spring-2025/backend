package com.capstone.dfms.responses;

import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.models.TaskTypeEntity;
import com.capstone.dfms.models.VaccineInjectionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    private TaskTypeEntity taskTypeId;
    private String assignerName;
    private String assigneeName;
    private String priority;
    private String shift;
    private String completionNotes;
    private ReportTaskEntity reportTask;
    private IllnessDetailEntity illness;
    private VaccineInjectionEntity vaccineInjection;


}
