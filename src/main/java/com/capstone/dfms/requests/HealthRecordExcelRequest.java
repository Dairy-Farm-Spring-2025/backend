package com.capstone.dfms.requests;

import com.alibaba.excel.annotation.ExcelProperty;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HealthRecordExcelRequest {
    @NotNull(message = "Cow Name is required")
    @ExcelProperty("Cow Name")
    private String cowName;

    @NotBlank(message = "Status is required")
    @ExcelProperty("Status")
    private String status;

    @NotNull(message = "Size is required")
    @ExcelProperty("Size")
    private Float size;

    @NotBlank(message = "Period is required")
    @ExcelProperty("Period")
    private String period;

    @NotNull(message = "Body Temperature is required")
    @ExcelProperty("Body Temperature")
    private Float bodyTemperature;

    @NotNull(message = "Heart Rate is required")
    @ExcelProperty("Heart Rate")
    private Float heartRate;

    @NotNull(message = "Respiratory Rate is required")
    @ExcelProperty("Respiratory Rate")
    private Float respiratoryRate;

    @NotNull(message = "Ruminate Activity is required")
    @ExcelProperty("Ruminate Activity")
    private Float ruminateActivity;

    @NotNull(message = "Chest Circumference is required")
    @ExcelProperty("Chest Circumference")
    private Float chestCircumference;

    @NotNull(message = "Body Length is required")
    @ExcelProperty("Body Length")
    private Float bodyLength;

    @ExcelProperty("Description")
    private String description;

    public CowStatus getCowStatus() {
        return CowStatus.fromString(period);
    }

    public HealthRecordStatus getHealthRecordStatus() {
        return HealthRecordStatus.fromString(status);
    }
}
