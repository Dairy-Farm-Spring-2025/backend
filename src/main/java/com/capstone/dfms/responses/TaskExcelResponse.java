package com.capstone.dfms.responses;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskExcelResponse {
    @ExcelProperty("Task type")
    private String taskType;

    @ExcelProperty("Area")
    private String area;

    @ExcelProperty("From date")
    private String fromDate;

    @ExcelProperty("To date")
    private String toDate;

    @ExcelProperty("Description")
    private String description;

    @ExcelProperty("Shift")
    private String shift;

    @ExcelIgnore
    private boolean error;

    @ExcelIgnore
    private String errorMessage;
}
