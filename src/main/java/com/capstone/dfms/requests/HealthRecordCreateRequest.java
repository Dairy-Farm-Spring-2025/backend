package com.capstone.dfms.requests;

import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.HealthRecordStatus;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthRecordCreateRequest {
    private HealthRecordStatus status;
    private String description;
    private Float size;
    private Float bodyTemperature;
    private Float heartRate;
    private Float respiratoryRate;
    private Float ruminateActivity;
    private Float chestCircumference;
    private Float bodyLength;
}
