package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableUserRequest {
    private Long roleId;

    private Long areaId;

    private LocalDate fromDate;

    private LocalDate toDate;
}
