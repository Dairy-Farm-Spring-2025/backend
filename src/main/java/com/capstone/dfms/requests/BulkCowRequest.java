package com.capstone.dfms.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkCowRequest {
    List<CowExcelCreateRequest> cows;
    List<HealthRecordExcelRequest> healthRecords;
}
