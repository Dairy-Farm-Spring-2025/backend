package com.capstone.dfms.services;

import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.requests.IllnessCreateRequest;
import com.capstone.dfms.requests.IllnessDetailPlanRequest;
import com.capstone.dfms.requests.IllnessDetailReportRequest;
import com.capstone.dfms.requests.IllnessDetailUpdateRequest;
import com.capstone.dfms.responses.CowPenBulkResponse;

import java.util.List;

public interface IIllnessDetailService {
    IllnessDetailEntity createIllnessDetail(IllnessDetailEntity detail, boolean isVet);
    List<IllnessDetailEntity> getAllIllnessDetails();
    IllnessDetailEntity getIllnessDetailById(Long id);
    List<IllnessDetailEntity> getIllnessDetailsByIllnessId(Long illnessId);
    IllnessDetailEntity updateIllnessDetail(Long id, IllnessDetailUpdateRequest updatedDetail);
    void deleteIllnessDetail(Long id);
    CowPenBulkResponse<IllnessDetailEntity> createTreatmentPlan(List<IllnessDetailPlanRequest> createRequests);
    IllnessDetailEntity reportTreatment(Long id, IllnessDetailReportRequest request);

}
