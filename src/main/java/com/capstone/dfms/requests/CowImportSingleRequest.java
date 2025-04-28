package com.capstone.dfms.requests;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CowImportSingleRequest {
    private CowCreateRequest cow;
    private HealthRecordCreateRequest healthRecord;
}
