package com.capstone.dfms.services;

import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.responses.CowResponse;

import java.util.List;

public interface ICowServices {
    CowResponse createCow(CowEntity request);
    CowResponse updateCow(Long id, CowEntity request);
    void deleteCow(Long id);
    CowResponse getCowById(Long id);
    List<CowResponse> getAllCows();
}
