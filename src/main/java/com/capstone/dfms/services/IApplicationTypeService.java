package com.capstone.dfms.services;

import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.requests.ApplicationTypeRequest;

import java.util.List;

public interface IApplicationTypeService  {
    ApplicationTypeEntity createApplicationType(ApplicationTypeRequest request);
    List<ApplicationTypeEntity> getAllApplicationTypes();
    ApplicationTypeEntity getApplicationTypeById(Long id);
    ApplicationTypeEntity updateApplicationType(Long id, ApplicationTypeRequest request);
    void deleteApplicationType(Long id);

}
