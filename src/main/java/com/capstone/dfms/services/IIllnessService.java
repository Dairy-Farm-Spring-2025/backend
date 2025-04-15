package com.capstone.dfms.services;


import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.models.enums.IllnessStatus;
import com.capstone.dfms.requests.IllnessCreateRequest;
import com.capstone.dfms.requests.IllnessPrognosisRequest;
import com.capstone.dfms.requests.IllnessUpdateRequest;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IIllnessService{
    IllnessEntity createIllness(IllnessEntity illness, List<MultipartFile> mediaFiles) throws IOException;
    List<IllnessEntity> getAllIllnesses();
    List<IllnessEntity> getIllnessByStatus(IllnessStatus status);
    IllnessEntity getIllnessById(Long id);
    List<IllnessEntity> getIllnessesByCowId(Long cowId);
//    IllnessEntity updateIllness(Long id, IllnessUpdateRequest updatedIllness);
    IllnessEntity updateIllness(Long id, IllnessUpdateRequest updatedIllness, @Nullable Boolean isPrognosis);

    void deleteIllness(Long id);
    IllnessEntity reportIllness(IllnessEntity illness, List<MultipartFile> mediaFiles) throws IOException;
    IllnessEntity prognosisIllness (Long id, IllnessPrognosisRequest request);

    IllnessEntity getIllnessWithDetail(Long id);

    IllnessEntity createIllness(IllnessCreateRequest request, List<MultipartFile> mediaFiles) throws IOException;
}
