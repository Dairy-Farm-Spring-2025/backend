package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.mappers.IIllnessDetailMapper;
import com.capstone.dfms.mappers.IIllnessMapper;
import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.IllnessDetailStatus;
import com.capstone.dfms.models.enums.IllnessStatus;
import com.capstone.dfms.repositories.IIllnessDetailRepository;
import com.capstone.dfms.repositories.IIllnessRepository;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.services.IIllnessDetailService;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class IllnessDetailService implements IIllnessDetailService {

    private final IIllnessDetailRepository illnessDetailRepository;
    private final IUserRepository userRepository;
    private final IItemRepository iItemRepository;
    private final IIllnessRepository illnessRepository;

    private final IIllnessDetailMapper mapper;

    @Override
    public IllnessDetailEntity createIllnessDetail(IllnessDetailEntity detail, boolean isVet) {
        IllnessEntity illness = null;
        ItemEntity itemEntity = null;
        UserEntity userEntity = null;

        if(detail.getIllnessEntity().getIllnessId() != null){
            Long id = detail.getIllnessEntity().getIllnessId();
            illness = illnessRepository.findById(id)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no illness have id" + id));
            detail.setIllnessEntity(illness);
        }

        if(detail.getVaccine().getItemId() != null){
            Long id = detail.getVaccine().getItemId();
            itemEntity = iItemRepository.findById(id)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no item have id" + id));
            if(!itemEntity.getCategoryEntity().getName().equalsIgnoreCase("vaccine"))
                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
            detail.setVaccine(itemEntity);
        }

        if(detail.getVeterinarian() != null){
            Long id = detail.getVeterinarian().getId();
            userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no user have id" + id));
            if(!userEntity.getRoleId().getName().equalsIgnoreCase("VETERINARIANS"))
                throw new AppException(HttpStatus.BAD_REQUEST, "NOT VETERINARIANS");
            detail.setVeterinarian(userEntity);
        }

        if(isVet){
            detail.setVeterinarian(UserStatic.getCurrentUser());
        }

        return illnessDetailRepository.save(detail);
    }

    @Override
    public List<IllnessDetailEntity> getAllIllnessDetails() {
        return illnessDetailRepository.findAll();
    }

    @Override
    public IllnessDetailEntity getIllnessDetailById(Long id) {
        return illnessDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no illness detail with id: " + id));
    }

    @Override
    public List<IllnessDetailEntity> getIllnessDetailsByIllnessId(Long illnessId) {
        List<IllnessDetailEntity> illnessDetailEntities = illnessDetailRepository.findByIllnessEntityIllnessId(illnessId);

        illnessDetailEntities.sort(Comparator.comparing(IllnessDetailEntity::getDate));

        return illnessDetailEntities;
    }

    @Override
    public IllnessDetailEntity updateIllnessDetail(Long id, IllnessDetailUpdateRequest updatedDetail) {
        ItemEntity itemEntity = null;

        if(updatedDetail.getItemId() != null){
            Long tempId = updatedDetail.getItemId();
            itemEntity = iItemRepository.findById(tempId)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no item have id" + tempId));
            if(!itemEntity.getCategoryEntity().getName().equalsIgnoreCase("vaccine"))
                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
        }

        IllnessDetailEntity oldIllnessDetail = this.getIllnessDetailById(id);
        mapper.updateEntityFromDto(updatedDetail, oldIllnessDetail);

        UserEntity currentUser = UserStatic.getCurrentUser();
        if(!currentUser.getRoleId().getName().equalsIgnoreCase("VETERINARIANS"))
            throw new AppException(HttpStatus.BAD_REQUEST, "Veterinarians role is required!");

        oldIllnessDetail.setVeterinarian(currentUser);
        oldIllnessDetail.setVaccine(itemEntity);

        illnessDetailRepository.save(oldIllnessDetail);

        if(oldIllnessDetail.getStatus().equals(IllnessDetailStatus.cured)){
            List<IllnessDetailEntity> illnessDetailEntities = this.getIllnessDetailsByIllnessId(oldIllnessDetail.getIllnessEntity().getIllnessId());

            for(IllnessDetailEntity illnessDetail : illnessDetailEntities){
                if(illnessDetail.getDate().isAfter(oldIllnessDetail.getDate())){
                    illnessDetail.setStatus(IllnessDetailStatus.cancel);
                    illnessDetailRepository.save(illnessDetail);
                }
            }

            IllnessEntity illness = oldIllnessDetail.getIllnessEntity();
            illness.setEndDate(LocalDate.now());
            illness.setIllnessStatus(IllnessStatus.complete);

            illnessRepository.save(illness);
        }

        return oldIllnessDetail;
    }

    @Override
    public void deleteIllnessDetail(Long id) {
        illnessDetailRepository.deleteById(id);
    }

    @Override
    public CowPenBulkResponse<IllnessDetailEntity> createTreatmentPlan(List<IllnessDetailPlanRequest> createRequests) {
        // Validate that the request list is not empty.
        if (createRequests == null || createRequests.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "The request list cannot be empty.");
        }

        // Validate that all requests have the same illnessId.
        Long baseIllnessId = createRequests.get(0).getIllnessId();
        for (IllnessDetailPlanRequest request : createRequests) {
            if (!baseIllnessId.equals(request.getIllnessId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, "All requests must have the same illnessId.");
            }
        }

        // Prepare lists for successes and errors.
        List<IllnessDetailEntity> successes = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Process each request individually.
        for (IllnessDetailPlanRequest request : createRequests) {
            try {
                // Call the create function of IllnessDetailService.
                IllnessDetailEntity createdEntity = this.createIllnessDetail(mapper.toModel(request), true);
                successes.add(createdEntity);
            } catch (Exception ex) {
                // Collect error messages but continue processing.
                String errorMessage = "Failed to create illness detail for date " + request.getDate()
                        + " with illnessId " + request.getIllnessId() + ": " + ex.getMessage();
                errors.add(errorMessage);
                // Optionally, log the error using your logging framework.
                System.err.println(errorMessage);
            }
        }

        // Sort successes by date in ascending order.
        successes.sort(Comparator.comparing(IllnessDetailEntity::getDate));

        // Return the bulk response with successes and errors.
        return CowPenBulkResponse.<IllnessDetailEntity>builder()
                .successes(successes)
                .errors(errors)
                .build();
    }

    @Override
    public IllnessDetailEntity reportTreatment(Long id, IllnessDetailReportRequest request) {
        IllnessDetailEntity oldIllnessDetail = this.getIllnessDetailById(id);
        if(!oldIllnessDetail.getDate().equals(LocalDate.now())){
            throw new AppException(HttpStatus.BAD_REQUEST, "No time to report!");
        }

        mapper.updateEntityFromDto(request, oldIllnessDetail);
        return oldIllnessDetail;
    }

}
