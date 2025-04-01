package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.PriorityTask;
import com.capstone.dfms.models.enums.TaskShift;
import com.capstone.dfms.models.enums.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskRequest {
    private String description;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Long areaId;
    private PriorityTask priority;
    private List<LocalDate> offDates;
}
