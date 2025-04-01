package com.capstone.dfms.responses;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.ReportTaskEntity;
import com.capstone.dfms.models.TaskTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RangeTaskResponse {
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
    private MaterialResponse material;
}
