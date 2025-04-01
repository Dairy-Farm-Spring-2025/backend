package com.capstone.dfms.responses;

import com.capstone.dfms.models.*;
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
    private AreaEntity areaId;
    private TaskTypeEntity taskTypeId;
    private String assignerName;
    private String assigneeName;
    private String priority;
    private String shift;
    private String completionNotes;
    private ReportTaskEntity reportTask;
    private IllnessDetailEntity illnessDetail;
    private VaccineInjectionEntity vaccineInjection;
    private IllnessEntity illness;


}
