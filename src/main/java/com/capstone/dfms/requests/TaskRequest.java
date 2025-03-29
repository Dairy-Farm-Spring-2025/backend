package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.PriorityTask;
import com.capstone.dfms.models.enums.TaskShift;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long areaId;
    private List<Long> assigneeIds;
    private PriorityTask priority;
    private Long taskTypeId;
    private TaskShift shift;
    private Long illnessId;
}
