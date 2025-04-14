package com.capstone.dfms.services;

import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.requests.PenUpdateRequest;
import com.capstone.dfms.responses.PenResponse;
import com.capstone.dfms.responses.PenStatusCountResponse;

import java.time.LocalDate;
import java.util.List;

public interface IPenServices {
    PenResponse createPen(PenEntity request);
    PenResponse updatePen(Long id, PenUpdateRequest request);
    void deletePen(Long id);
    PenResponse getPenById(Long id);
    List<PenResponse> getAllPens();
    List<PenEntity> getAvailablePens(LocalDate currentDate);
    PenStatusCountResponse getPenStatusCountByArea(Long areaId);
    List<PenEntity> getPenByArea(Long areaId);
    List<PenResponse> getPensByCowTypeAndStatus(Long cowTypeId, CowStatus cowStatus);
}
