package com.capstone.dfms.services;

import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;

import java.util.List;

public interface IAreaServices {
    AreaResponse createArea(AreaEntity request);
    AreaResponse updateArea(Long id, AreaUpdateRequest request);
    void deleteArea(Long id);
    AreaResponse getAreaById(Long id);
    List<AreaResponse> getAllAreas();
}
