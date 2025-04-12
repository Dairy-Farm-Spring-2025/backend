package com.capstone.dfms.responses;

import com.capstone.dfms.models.HealthRecordEntity;
import com.capstone.dfms.requests.CowExcelCreateRequest;
import com.capstone.dfms.requests.HealthRecordExcelRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulkCowHealthRecordResponse {
    BulkResponse<CowExcelCreateRequest> cowResponseCowPenBulkResponse;
    BulkResponse<HealthRecordExcelRequest> healthRecordEntityCowPenBulkResponse;
}
