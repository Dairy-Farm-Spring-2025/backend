package com.capstone.dfms.services;

import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.responses.PenResponse;

import java.time.LocalDate;
import java.util.List;

public interface IPenServices {
    PenResponse createPen(PenEntity request);
    PenResponse updatePen(Long id, PenEntity request);
    void deletePen(Long id);
    PenResponse getPenById(Long id);
    List<PenResponse> getAllPens();
    List<PenEntity> getAvailablePens(LocalDate currentDate);
}
