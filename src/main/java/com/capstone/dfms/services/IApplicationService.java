package com.capstone.dfms.services;

import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.ApplicationCreateRequest;

import java.util.List;

public interface IApplicationService {
    ApplicationEntity createApplication(ApplicationCreateRequest request);
    ApplicationEntity getApplicationById(Long applicationId);
    List<ApplicationEntity> getApplications();

    ApplicationEntity updateApplication(Long applicationId, ApplicationApproveRequest request);

    void deleteApplication(Long applicationId);

    ApplicationEntity cancelApplication(Long applicationId, ApplicationApproveRequest request);
    List<ApplicationEntity> getApplicationsByApplicationType(Long applicationTypeId);
    List<ApplicationEntity> getApplicationsByRequestBy();
}
