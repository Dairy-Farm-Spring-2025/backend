package com.capstone.dfms.responses;

import com.capstone.dfms.models.ReportTaskImageEntity;
import com.capstone.dfms.models.TaskEntity;
import com.capstone.dfms.models.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTaskResponse {
    private Long reportTaskId;

    private String description;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDate date;

    private String comment;

    private UserEntity reviewer_id;

    private List<ReportTaskImageEntity> reportImages;

    private TaskEntity taskId;
}
