package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.IIllnessDetailMapper;
import com.capstone.dfms.mappers.IIllnessMapper;
import com.capstone.dfms.models.*;
import com.capstone.dfms.models.enums.*;
import com.capstone.dfms.repositories.*;
import com.capstone.dfms.requests.*;
import com.capstone.dfms.services.IIllnessService;
import com.capstone.dfms.services.INotificationService;
import com.capstone.dfms.services.IUserService;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class IllnessService implements IIllnessService {
    private final IIllnessRepository illnessRepository;
    private final ICowRepository cowRepository;
    private final IIllnessMapper iIllnessMapper;
    private final IItemRepository iItemRepository;
    private final IIllnessDetailMapper illnessDetailMapper;
    private final ITaskRepository taskRepository;
    private final ITaskTypeRepository taskTypeRepository;
    private final ICowPenRepository cowPenRepository;
    private final IRoleRepository roleRepository;
    private final IHealthRecordRepository healthRecordRepository;
    private final IIllnessDetailMapper mapper;
    private final IUserRepository userRepository;
    private final INotificationService notificationService;



    @Override
    public IllnessEntity createIllness(IllnessEntity illness) {
        CowEntity cowEntity = this.findCowEntity(illness.getCowEntity().getCowId());
        illness.setCowEntity(cowEntity);
        illness.setUserEntity(UserStatic.getCurrentUser());
        illness.setIllnessStatus(IllnessStatus.pending);
        return illnessRepository.save(illness);
    }

    @Override
    public List<IllnessEntity> getAllIllnesses() {
        return illnessRepository.findAll();
    }

    @Override
    public List<IllnessEntity> getIllnessByStatus(IllnessStatus status) {
        return illnessRepository.findByIllnessStatus(status);
    }

    @Override
    public IllnessEntity getIllnessById(Long id) {
        return illnessRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.not.found")));
    }

    @Override
    public List<IllnessEntity> getIllnessesByCowId(Long cowId) {
        return illnessRepository.findByCowEntityCowId(cowId);
    }

//    @Override
//    public IllnessEntity updateIllness(Long id, IllnessUpdateRequest updatedIllness) {
//        CowEntity cowEntity = this.findCowEntity(updatedIllness.getCowId());
//        IllnessEntity oldIllness = this.getIllnessById(id);
//
//        iIllnessMapper.updateIllnessEntityFromDto(updatedIllness, oldIllness);
//
//        return illnessRepository.save(oldIllness);
//    }

    @Override
    public IllnessEntity updateIllness(Long id, IllnessUpdateRequest updatedIllness, Boolean isPrognosis) {
        CowEntity cowEntity = null;
        if(updatedIllness.getCowId() != null)
            cowEntity = this.findCowEntity(updatedIllness.getCowId());
        IllnessEntity oldIllness = this.getIllnessById(id);

        if(!(oldIllness.getIllnessStatus() == IllnessStatus.pending)){
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.not.update")
            );
        }

        iIllnessMapper.updateIllnessEntityFromDto(updatedIllness, oldIllness);

        if(isPrognosis){
            oldIllness.setVeterinarian(UserStatic.getCurrentUser());
        }

        if(updatedIllness.getSeverity() == IllnessSeverity.none){
            oldIllness.setIllnessStatus(IllnessStatus.cancel);
            oldIllness.setEndDate(LocalDate.now());
        }
        else{
            oldIllness.setIllnessStatus(IllnessStatus.processing);

            cowEntity = oldIllness.getCowEntity();
            cowEntity.setCowStatus(CowStatus.sickCow);
            cowEntity.setDateOfOut(LocalDate.now());
            cowRepository.save(cowEntity);
        }

        return illnessRepository.save(oldIllness);
    }

    @Override
    public void deleteIllness(Long id) {
        illnessRepository.deleteById(id);
    }

    //-------------------MAIN FUNCTION---------------------
    @Override
    public IllnessEntity reportIllness(IllnessEntity illness) {
        LocalDate currentDate = LocalDate.now();
        illness.setStartDate(currentDate);

        IllnessEntity savedIllness = this.createIllness(illness);

        RoleEntity role = roleRepository.findById(2L).orElseThrow(() ->
                new AppException(HttpStatus.NOT_FOUND,
                        LocalizationUtils.getMessage("user.login.role_not_exist")));
        List<Long> managerIds = userRepository.findByRoleId(role.getId())
                .stream().map(UserEntity::getId).toList();

        if (!managerIds.isEmpty()) {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setTitle("Báo cáo bệnh mới");
            notificationRequest.setDescription("Một báo cáo bệnh mới đã được tạo.");
            notificationRequest.setLink("/illness/" + savedIllness.getIllnessId());
            notificationRequest.setCategory(CategoryNotification.heathcare);
            notificationRequest.setUserIds(managerIds);

            notificationService.createNotification(notificationRequest);
        }
        return savedIllness;
    }

    @Override
    public IllnessEntity prognosisIllness(Long id, IllnessPrognosisRequest request) {
        IllnessUpdateRequest updateRequest = new IllnessUpdateRequest();
        updateRequest.setPrognosis(request.getPrognosis());
        updateRequest.setSeverity(request.getSeverity());

        return this.updateIllness(id, updateRequest, true);
    }

    @Override
    public IllnessEntity getIllnessWithDetail(Long id) {
        return illnessRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.not.found")));
    }

    @Override
    public IllnessEntity createIllness(IllnessCreateRequest request) {
        if (request.getSeverity().equals(IllnessSeverity.none)){
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("illness.severity.none"));
        }

        IllnessEntity illness = iIllnessMapper.toModel(request);

        // Ensure relations are set properly
        illness.setCowEntity(findCowEntity(request.getCowId()));
        illness.setUserEntity(UserStatic.getCurrentUser());
        illness.setIllnessStatus(IllnessStatus.processing);
        illness.setStartDate(LocalDate.now());

        if (request.getDetail() != null) {
            List<IllnessDetailEntity > illnessDetails = new ArrayList<>();
            request.getDetail().forEach(detail -> {
                IllnessDetailEntity illnessDetail = illnessDetailMapper.toModel(detail);
                illnessDetail.setStatus(IllnessDetailStatus.pending);
                illnessDetail.setIllnessEntity(illness);


                Long id = detail.getVaccineId();
                var itemEntity = iItemRepository.findById(id)
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("item.not_exist")
                        ));
                illnessDetail.setVaccine(itemEntity);

                illnessDetail.setDescription("Điều trị bệnh cho: " + illness.getCowEntity().getName() +
                        " - Vaccine: " + itemEntity.getName());

                illnessDetails.add(illnessDetail);
            });

            illness.setIllnessDetails(illnessDetails);
        }

        IllnessEntity createdEntity = illnessRepository.save(illness);

        // 🔹 Create Tasks for Each Illness Detail
        for (IllnessDetailEntity detail : createdEntity.getIllnessDetails()) {
            createTaskForIllnessDetail(createdEntity, detail);
        }

        return createdEntity;
    }




    //-----------------------------------------------------
    private CowEntity findCowEntity(Long cowId){
        CowEntity cowEntity = cowRepository.findById(cowId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.not.found")));
        return cowEntity;
    }

    private void createTaskForIllnessDetail(IllnessEntity illness, IllnessDetailEntity detail) {
        RoleEntity role = roleRepository.findById(3L).orElseThrow(() ->
                new AppException(HttpStatus.NOT_FOUND,
                        LocalizationUtils.getMessage("user.login.role_not_exist")));

        TaskTypeEntity treatmentTaskType = taskTypeRepository.findByName("Chữa bệnh")
                .orElseGet(() -> {
                    TaskTypeEntity newTaskType = new TaskTypeEntity();
                    newTaskType.setName("Chữa bệnh");
                    newTaskType.setRoleId(role);
                    newTaskType.setDescription("Công việc điều trị bệnh cho bò");
                    return taskTypeRepository.save(newTaskType);
                });

        CowEntity cow = illness.getCowEntity();
        CowPenEntity latestCowPen = cowPenRepository.latestCowPenByCowId(cow.getCowId());

        TaskEntity task = new TaskEntity();
        task.setDescription("Điều trị bệnh cho: " + cow.getName() +
                " - Vaccine: " + detail.getVaccine().getName());
        task.setStatus(TaskStatus.pending);
        task.setFromDate(detail.getDate());
        task.setToDate(detail.getDate());
        task.setShift(TaskShift.dayShift);
        task.setTaskTypeId(treatmentTaskType);
        task.setAreaId(latestCowPen.getPenEntity().getAreaBelongto());
        task.setIllness(detail);

        taskRepository.save(task);
    }



}
