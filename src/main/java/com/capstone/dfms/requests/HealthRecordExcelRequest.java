package com.capstone.dfms.requests;

import com.alibaba.excel.annotation.ExcelProperty;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import lombok.Data;

@Data
public class HealthRecordExcelRequest {

    @ExcelProperty("Cow Name")
    private String cowName; // You may need to map this to cowId later

    @ExcelProperty("Status")
    private String status;

    @ExcelProperty("Size")
    private float size;

    @ExcelProperty("Period")
    private String period;

    @ExcelProperty("Body Temperature")
    private float bodyTemperature;

    @ExcelProperty("Heart Rate")
    private float heartRate;

    @ExcelProperty("Respiratory Rate")
    private float respiratoryRate;

    @ExcelProperty("Ruminate Activity")
    private float ruminateActivity;

    @ExcelProperty("Chest Circumference")
    private float chestCircumference;

    @ExcelProperty("Body Length")
    private float bodyLength;

    @ExcelProperty("Description")
    private String description;

    public CowStatus getCowStatus() {
        return CowStatus.fromString(period);
    }

    public HealthRecordStatus getHealthRecordStatus() {
        return HealthRecordStatus.fromString(status);
    }
}
