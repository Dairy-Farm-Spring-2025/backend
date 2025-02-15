package com.capstone.dfms.services;


import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.requests.IllnessPrognosisRequest;
import com.capstone.dfms.requests.IllnessUpdateRequest;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public interface IIllnessService{
    IllnessEntity createIllness(IllnessEntity illness);
    List<IllnessEntity> getAllIllnesses();
    IllnessEntity getIllnessById(Long id);
    List<IllnessEntity> getIllnessesByCowId(Long cowId);
//    IllnessEntity updateIllness(Long id, IllnessUpdateRequest updatedIllness);
    IllnessEntity updateIllness(Long id, IllnessUpdateRequest updatedIllness, @Nullable Boolean isPrognosis);

    void deleteIllness(Long id);
    IllnessEntity reportIllness(IllnessEntity illness);
    IllnessEntity prognosisIllness (Long id, IllnessPrognosisRequest request);

    IllnessEntity getIllnessWithDetail(Long id);
}
