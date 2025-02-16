package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.mappers.IApplicationMapper;
import com.capstone.dfms.models.ApplicationEntity;
import com.capstone.dfms.models.ApplicationTypeEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.ApplicationStatus;
import com.capstone.dfms.models.enums.ApprovalStatus;
import com.capstone.dfms.repositories.IApplicationRepository;
import com.capstone.dfms.repositories.IApplicationTypeRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.ApplicationApproveRequest;
import com.capstone.dfms.requests.ApplicationCreateRequest;
import com.capstone.dfms.services.IApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationService implements IApplicationService {
    private final IApplicationRepository applicationRepository;
    private final IApplicationTypeRepository applicationTypeRepository;
    private final IUserRepository userRepository;
    private final IApplicationMapper applicationMapper;

    @Override
    public ApplicationEntity createApplication(ApplicationCreateRequest request) {
        ApplicationTypeEntity type = applicationTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Application Type not found"));

        // Validate requestBy user
        UserEntity requestBy = UserStatic.getCurrentUser();

        boolean applicationExists = applicationRepository.existsByRequestByAndFromDateAndToDateAndType(
                requestBy, request.getFromDate(), request.getToDate(), type);

        if (applicationExists) {
            throw new RuntimeException("A similar application already exists. Please modify your request.");
        }

        // Map request to entity
        ApplicationEntity application = applicationMapper.toModel(request);

        // Set references (if not already handled in mapper)
        application.setType(type);
        application.setRequestBy(requestBy);

        // Save to database
        return applicationRepository.save(application);
    }

    @Override
    public ApplicationEntity getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application with ID " + applicationId + " not found."));
    }

    @Override
    public List<ApplicationEntity> getApplications() {
        return applicationRepository.findAll();
    }

    @Override
    public ApplicationEntity updateApplication(Long applicationId, ApplicationApproveRequest request) {
        ApplicationEntity approvedApplication = this.getApplicationById(applicationId);
        applicationMapper.updateEntityFromDto(request, approvedApplication);

        if(request.getApprovalStatus().equals(ApprovalStatus.approve)){
            approvedApplication.setStatus(ApplicationStatus.complete);
        }
        else if (request.getApprovalStatus().equals(ApprovalStatus.reject)){
            approvedApplication.setStatus(ApplicationStatus.reject);
        }

        UserEntity approveBy = UserStatic.getCurrentUser();
        approvedApplication.setApproveBy(approveBy);

        return applicationRepository.save(approvedApplication);
    }

    @Override
    public void deleteApplication(Long applicationId) {
        ApplicationEntity deletedApplication = this.getApplicationById(applicationId);
        applicationRepository.delete(deletedApplication);
    }

    @Override
    public ApplicationEntity cancelApplication(Long applicationId) {
        ApplicationEntity cancelApplication = this.getApplicationById(applicationId);
        UserEntity currentUser = UserStatic.getCurrentUser();

        if(cancelApplication.getStatus() != ApplicationStatus.processing){
            throw new AppException(HttpStatus.BAD_REQUEST, "Application was approved/rejected!");
        }

        if(!(cancelApplication.getRequestBy().getId() == currentUser.getId()
                || currentUser.getRoleId().getName().equalsIgnoreCase("MANAGER"))){
            throw new AppException(HttpStatus.BAD_REQUEST, "You don't have permission to cancel application!");
        }

        cancelApplication.setStatus(ApplicationStatus.cancel);

        return applicationRepository.save(cancelApplication);
    }

    @Override
    public List<ApplicationEntity> getApplicationsByApplicationType(Long applicationTypeId) {
        ApplicationTypeEntity applicationType = applicationTypeRepository.findById(applicationTypeId)
                .orElseThrow(() -> new RuntimeException("Application Type with ID " + applicationTypeId + " not found."));

        return applicationRepository.findByType(applicationType);
    }

    @Override
    public List<ApplicationEntity> getApplicationsByRequestBy() {
        UserEntity requestBy = UserStatic.getCurrentUser();

        return applicationRepository.findByRequestBy(requestBy);
    }
}
