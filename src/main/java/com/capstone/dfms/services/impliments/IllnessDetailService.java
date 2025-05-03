package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.components.utils.CowUtlis;
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
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.not.found")));
            this.checkValidateTreatmentPlan(illness);
            detail.setIllnessEntity(illness);
        }

        CowUtlis.validateCow(illness.getCowEntity());

        if(detail.getVaccine().getItemId() != null){
            Long id = detail.getVaccine().getItemId();
            itemEntity = iItemRepository.findById(id)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_exist")));

            detail.setVaccine(itemEntity);
        }

        if(detail.getVeterinarian() != null){
            Long id = detail.getVeterinarian().getId();
            userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("user.not_exist")));
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
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.detail.not.found")));
    }

    @Override
    public List<IllnessDetailEntity> getIllnessDetailsByIllnessId(Long illnessId) {
        List<IllnessDetailEntity> illnessDetailEntities = illnessDetailRepository.findByIllnessEntityIllnessId(illnessId);

        illnessDetailEntities.sort(Comparator.comparing(IllnessDetailEntity::getDate));

        return illnessDetailEntities;
    }

    @Override
    public IllnessDetailEntity updateIllnessDetail(Long id, IllnessDetailUpdateRequest updatedDetail) {
        IllnessDetailEntity oldIllnessDetail = this.getIllnessDetailById(id);

        CowUtlis.validateCow(oldIllnessDetail.getIllnessEntity().getCowEntity());
        mapper.updateEntityFromDto(updatedDetail, oldIllnessDetail);

        this.checkValidateTreatmentPlan(oldIllnessDetail.getIllnessEntity());

        UserEntity currentUser = UserStatic.getCurrentUser();
        oldIllnessDetail.setVeterinarian(currentUser);

        if(updatedDetail.getItemId() != null){
            Long tempId = updatedDetail.getItemId();
            ItemEntity itemEntity = iItemRepository.findById(tempId)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_exist")));
            if(!itemEntity.getCategoryEntity().getName().equalsIgnoreCase("Vắc-xin")
                    || !itemEntity.getCategoryEntity().getName().equalsIgnoreCase("Thuốc"))
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not.vaccine"));
            oldIllnessDetail.setVaccine(itemEntity);
        }

        oldIllnessDetail = illnessDetailRepository.save(oldIllnessDetail);

        if(oldIllnessDetail.getStatus().equals(IllnessDetailStatus.cured)){
            cancelNextIllnessDetail(oldIllnessDetail.getIllnessEntity().getIllnessId(), oldIllnessDetail.getDate());

            IllnessEntity illness = oldIllnessDetail.getIllnessEntity();
            illness.setEndDate(LocalDate.now());
            illness.setIllnessStatus(IllnessStatus.complete);
            illnessRepository.save(illness);
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
        if (createRequests == null || createRequests.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("request.list.empty"));
        }

        Long baseIllnessId = createRequests.get(0).getIllnessId();

        for (IllnessDetailPlanRequest request : createRequests) {
            if (!baseIllnessId.equals(request.getIllnessId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("request.illnessId.mismatch"));
            }
        }

        Optional<IllnessEntity> illness = illnessRepository.findById(baseIllnessId);
        if(illness.isPresent()){
            CowUtlis.validateCow(illness.get().getCowEntity());
        }
        else{
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.not.found"));
        }

        List<IllnessDetailEntity> successes = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (IllnessDetailPlanRequest request : createRequests) {
            for(LocalDate date = request.getDateFrom(); !date.isAfter(request.getDateTo()); date = date.plusDays(1)) {
                try {
                    // Call the create function of IllnessDetailService.
                    IllnessDetailEntity createdEntity = mapper.toModel(request);
                    createdEntity.setDate(date);
                    createdEntity = this.createIllnessDetail(createdEntity, true);
                    successes.add(createdEntity);
                    if (createdEntity.getDate().equals(LocalDate.now().plusDays(1))) {
                        RoleEntity role = roleRepository.findById(3L).orElseThrow(()
                                -> new AppException(HttpStatus.NOT_FOUND, LocalizationUtils.getMessage("user.login.role_not_exist")));

                        TaskTypeEntity treatmentTaskType = taskTypeRepository.findByName("Chữa bệnh")
                                .orElseGet(() -> {
                                    TaskTypeEntity newTaskType = new TaskTypeEntity();
                                    newTaskType.setName("Chữa bệnh");
                                    newTaskType.setRoleId(role);
                                    newTaskType.setDescription("Công việc điều trị bệnh cho bò");
                                    return taskTypeRepository.save(newTaskType);
                                });

                        CowEntity cow = createdEntity.getIllnessEntity().getCowEntity();
                        CowPenEntity latestCowPen = cowPenRepository.latestCowPenByCowId(cow.getCowId());

                        TaskEntity task = new TaskEntity();
                        task.setDescription("Điều trị bệnh: " + createdEntity.getDescription());
                        task.setStatus(TaskStatus.pending);
                        task.setPriority(PriorityTask.high);
                        task.setFromDate(createdEntity.getDate());
                        task.setToDate(createdEntity.getDate());
                        task.setIllness(createdEntity);
                        task.setShift(TaskShift.dayShift);
                        task.setTaskTypeId(treatmentTaskType);

                        if (latestCowPen != null && latestCowPen.getPenEntity() != null) {
                            task.setAreaId(latestCowPen.getPenEntity().getAreaBelongto());
                        }

                        taskRepository.save(task);

                    }
                } catch (Exception ex) {
                    String errorMessage = "Failed to create illness detail for date " + date
                            + " with illnessId " + request.getIllnessId() + ": " + ex.getMessage();
                    errors.add(errorMessage);
                    System.err.println(errorMessage);
                }
            }
        }

        successes.sort(Comparator.comparing(IllnessDetailEntity::getDate));

        return CowPenBulkResponse.<IllnessDetailEntity>builder()
                .successes(successes)
                .errors(errors)
                .build();
    }

    @Override
    public IllnessDetailEntity reportTreatment(Long id, IllnessDetailReportRequest request) {
        IllnessDetailEntity oldIllnessDetail = this.getIllnessDetailById(id);
        CowUtlis.validateCow(oldIllnessDetail.getIllnessEntity().getCowEntity());
        if(!oldIllnessDetail.getDate().equals(LocalDate.now())){
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("report.time.missing"));
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
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.report.restricted"));
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
                .findByCowEntityCowIdOrderByReportTimeDesc(cowId);

        for (HealthRecordEntity record : healthRecords) {
            if (record.getPeriod() != CowStatus.sickCow) {
                return record.getPeriod();
            }
        }

        return CowStatus.youngCow;
    }

}
