package com.capstone.dfms.services;

import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.responses.CowTypeResponse;

import java.util.List;

public interface ICowTypeServices {
    CowTypeResponse createCowType(CowTypeEntity request);
    CowTypeResponse updateCowType(Long id, CowTypeEntity request);
    void deleteCowType(Long id);
    CowTypeResponse getCowTypeById(Long id);
    List<CowTypeResponse> getAllCowTypes();
}
