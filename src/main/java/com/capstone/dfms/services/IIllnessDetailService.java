package com.capstone.dfms.services;

import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.requests.IllnessDetailUpdateRequest;

import java.util.List;

public interface IIllnessDetailService {
    IllnessDetailEntity createIllnessDetail(IllnessDetailEntity detail);
    List<IllnessDetailEntity> getAllIllnessDetails();
    IllnessDetailEntity getIllnessDetailById(Long id);
    List<IllnessDetailEntity> getIllnessDetailsByIllnessId(Long illnessId);
    IllnessDetailEntity updateIllnessDetail(Long id, IllnessDetailUpdateRequest updatedDetail);
    void deleteIllnessDetail(Long id);
}
