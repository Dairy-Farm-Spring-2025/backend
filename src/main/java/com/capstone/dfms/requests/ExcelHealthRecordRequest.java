package com.capstone.dfms.requests;

import com.alibaba.excel.annotation.ExcelProperty;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcelHealthRecordRequest {

    private String cowName; // You may need to map this to cowId later


    private HealthRecordStatus status;

    private float size;

    private CowStatus period;

    private float bodyTemperature;

    private float heartRate;

    private float respiratoryRate;

    private float ruminateActivity;

    private float chestCircumference;

    private float bodyLength;

    private String description;
}
