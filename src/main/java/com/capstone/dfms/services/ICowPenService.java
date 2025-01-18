package com.capstone.dfms.services;

import com.capstone.dfms.models.CowPenEntity;
import com.capstone.dfms.responses.CowPenResponse;

import java.time.LocalDate;
import java.util.List;

public interface ICowPenService {
    CowPenResponse create(CowPenEntity request);
    List<CowPenResponse> getAll();
    CowPenResponse getById(Long penId, Long cowId, LocalDate fromDate);
    CowPenResponse update(Long penId, Long cowId, LocalDate fromDate, CowPenEntity updatedRequest);
    void delete(Long penId, Long cowId, LocalDate fromDate);

    List<CowPenResponse> getCowPenFollowCowId(Long cowId);
    List<CowPenResponse> getCowPenFollowPenId(Long penId);
}
