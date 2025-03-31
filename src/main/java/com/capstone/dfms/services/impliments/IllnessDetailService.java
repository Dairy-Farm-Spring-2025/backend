package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.IIllnessDetailMapper;
import com.capstone.dfms.mappers.IIllnessMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.*;
import com.capstone.dfms.repositories.*;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class IllnessDetailService implements IIllnessDetailService {

    private final IIllnessDetailRepository illnessDetailRepository;
    private final IUserRepository userRepository;
    private final IItemRepository iItemRepository;
    private final IIllnessRepository illnessRepository;
    private final ICowRepository cowRepository;
    private final ITaskRepository taskRepository;
    private final ITaskTypeRepository taskTypeRepository;
    private final ICowPenRepository cowPenRepository;
    private final IRoleRepository roleRepository;
    private final IHealthRecordRepository healthRecordRepository;
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
            this.checkValidateTreatmentPlan(illness);
            detail.setIllnessEntity(illness);
        }

        if(detail.getVaccine().getItemId() != null){
            Long id = detail.getVaccine().getItemId();
            itemEntity = iItemRepository.findById(id)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no item have id" + id));
//            if(!itemEntity.getCategoryEntity().getName().equalsIgnoreCase("vaccine"))
//                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
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

        detail.setStatus(IllnessDetailStatus.pending);

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
        //Get updated Illness Detail
        IllnessDetailEntity oldIllnessDetail = this.getIllnessDetailById(id);
        mapper.updateEntityFromDto(updatedDetail, oldIllnessDetail);

        this.checkValidateTreatmentPlan(oldIllnessDetail.getIllnessEntity());

        UserEntity currentUser = UserStatic.getCurrentUser();
        oldIllnessDetail.setVeterinarian(currentUser);

        if(updatedDetail.getItemId() != null){
            Long tempId = updatedDetail.getItemId();
            ItemEntity itemEntity = iItemRepository.findById(tempId)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no item have id" + tempId));
            if(!itemEntity.getCategoryEntity().getName().equalsIgnoreCase("vaccine"))
                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
            oldIllnessDetail.setVaccine(itemEntity);
        }

        oldIllnessDetail = illnessDetailRepository.save(oldIllnessDetail);

        if(oldIllnessDetail.getStatus().equals(IllnessDetailStatus.cured)){
            cancelNextIllnessDetail(oldIllnessDetail.getIllnessEntity().getIllnessId(), oldIllnessDetail.getDate());

            IllnessEntity illness = oldIllnessDetail.getIllnessEntity();
            illness.setEndDate(LocalDate.now());
            illness.setIllnessStatus(IllnessStatus.complete);
            illnessRepository.save(illness);

            //return latest health record not sick
        }

        if(oldIllnessDetail.getStatus().equals(IllnessDetailStatus.deceased)){
            cancelNextIllnessDetail(oldIllnessDetail.getIllnessEntity().getIllnessId(), oldIllnessDetail.getDate());

            IllnessEntity illness = oldIllnessDetail.getIllnessEntity();
            illness.setEndDate(LocalDate.now());
            illness.setIllnessStatus(IllnessStatus.fail);
            illnessRepository.save(illness);

            CowEntity cowEntity = illness.getCowEntity();
            cowEntity.setCowStatus(CowStatus.culling);
            cowEntity.setDateOfOut(LocalDate.now());
            cowRepository.save(cowEntity);
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

        UserEntity currentUser = UserStatic.getCurrentUser();
        oldIllnessDetail.setVeterinarian(currentUser);

        oldIllnessDetail = illnessDetailRepository.save(oldIllnessDetail);

        if(oldIllnessDetail.getStatus().equals(IllnessDetailStatus.cured)){
            cancelNextIllnessDetail(oldIllnessDetail.getIllnessEntity().getIllnessId(), oldIllnessDetail.getDate());

            IllnessEntity illness = oldIllnessDetail.getIllnessEntity();
            illness.setEndDate(LocalDate.now());
            illness.setIllnessStatus(IllnessStatus.complete);
            illnessRepository.save(illness);

            CowEntity cowEntity = illness.getCowEntity();
            cowEntity.setCowStatus(getLatestHealthRecordByCowId(cowEntity.getCowId()));
            cowEntity.setDateOfOut(LocalDate.now());
            cowRepository.save(cowEntity);
        }

        if(oldIllnessDetail.getStatus().equals(IllnessDetailStatus.deceased)){
            cancelNextIllnessDetail(oldIllnessDetail.getIllnessEntity().getIllnessId(), oldIllnessDetail.getDate());

            IllnessEntity illness = oldIllnessDetail.getIllnessEntity();
            illness.setEndDate(LocalDate.now());
            illness.setIllnessStatus(IllnessStatus.fail);
            illnessRepository.save(illness);

            CowEntity cowEntity = illness.getCowEntity();
            cowEntity.setCowStatus(CowStatus.culling);
            cowEntity.setDateOfOut(LocalDate.now());
            cowRepository.save(cowEntity);
        }

        return oldIllnessDetail;
    }

    private void checkValidateTreatmentPlan(IllnessEntity illness){
        IllnessStatus illnessStatus = illness.getIllnessStatus();
        if(illnessStatus != null) {
            if (!illnessStatus.equals(IllnessStatus.processing)) {
                throw new AppException(HttpStatus.BAD_REQUEST, "No further to report illness treatment or create new plan!");
            }
        }
    }

    private void cancelNextIllnessDetail(Long illnessId, LocalDate date){
        List<IllnessDetailEntity> illnessDetailEntities = this.getIllnessDetailsByIllnessId(illnessId);

        for(IllnessDetailEntity illnessDetail : illnessDetailEntities){
            if(illnessDetail.getDate().isAfter(date)){
                illnessDetail.setStatus(IllnessDetailStatus.cancel);
                illnessDetailRepository.save(illnessDetail);
                TaskEntity task = taskRepository.findByIllness(illnessDetail);
                task.setStatus(TaskStatus.canceled);
                taskRepository.save(task);
            }
        }
    }

    private CowStatus getLatestHealthRecordByCowId(Long cowId) {
        List<HealthRecordEntity> healthRecords = healthRecordRepository
                .findByCowEntityCowIdOrderByReportTimeDesc(cowId); // Ensure ordering by latest first

        for (HealthRecordEntity record : healthRecords) {
            if (record.getPeriod() != CowStatus.sickCow) {
                return record.getPeriod(); // Return the first non-sickCow record (which is the latest one)
            }
        }

        return CowStatus.youngCow;
    }

}
