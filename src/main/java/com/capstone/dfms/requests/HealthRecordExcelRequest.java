package com.capstone.dfms.requests;

import com.alibaba.excel.annotation.ExcelProperty;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class HealthRecordExcelRequest {
    @NotNull(message = "Cow Name is required")
    @ExcelProperty("Cow Name")
    private String cowName;

    @NotBlank(message = "Status is required")
    @ExcelProperty("Status")
    private String status;

    @NotNull(message = "Size is required")
    @Positive(message = "Size must be positive")
    @ExcelProperty("Size")
    private Float size;

//    @NotBlank(message = "Period is required")
//    @ExcelProperty("Period")
//    private String period;

    @NotNull(message = "Body Temperature is required")
    @Positive(message = "Body Temperature must be positive")
    @ExcelProperty("Body Temperature")
    private Float bodyTemperature;


    @NotNull(message = "Heart Rate is required")
    @Positive(message = "Heart Rate must be positive")
    @ExcelProperty("Heart Rate")
    private Float heartRate;

    @NotNull(message = "Respiratory Rate is required")
    @Positive(message = "Respiratory Rate must be positive")
    @ExcelProperty("Respiratory Rate")
    private Float respiratoryRate;

    @NotNull(message = "Ruminate Activity is required")
    @Positive(message = "Ruminate Activity must be positive")
    @ExcelProperty("Ruminate Activity")
    private Float ruminateActivity;

    @NotNull(message = "Chest Circumference is required")
    @Positive(message = "Chest Circumference must be positive")
    @ExcelProperty("Chest Circumference")
    private Float chestCircumference;

    @NotNull(message = "Body Length is required")
    @Positive(message = "Body Length must be positive")
    @ExcelProperty("Body Length")
    private Float bodyLength;

    @ExcelProperty("Description")
    private String description;

    private List<String> errorString;

//    public CowStatus getCowStatus() {
//        return CowStatus.fromString(period);
//    }

    public HealthRecordStatus getHealthRecordStatus() {
        return HealthRecordStatus.fromString(status);
    }
}
