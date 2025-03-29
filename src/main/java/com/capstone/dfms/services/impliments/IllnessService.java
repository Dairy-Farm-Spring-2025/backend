package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.securities.UserPrincipal;
import com.capstone.dfms.components.statics.UserStatic;
import com.capstone.dfms.mappers.IIllnessDetailMapper;
import com.capstone.dfms.mappers.IIllnessMapper;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.models.enums.IllnessDetailStatus;
import com.capstone.dfms.models.enums.IllnessSeverity;
import com.capstone.dfms.models.enums.IllnessStatus;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IIllnessDetailRepository;
import com.capstone.dfms.repositories.IIllnessRepository;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.requests.IllnessCreateRequest;
import com.capstone.dfms.requests.IllnessDetailPlanVet;
import com.capstone.dfms.requests.IllnessPrognosisRequest;
import com.capstone.dfms.requests.IllnessUpdateRequest;
import com.capstone.dfms.services.IIllnessService;
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

@Service
@AllArgsConstructor
public class IllnessService implements IIllnessService {
    private final IIllnessRepository illnessRepository;
    private final ICowRepository cowRepository;
    private final IIllnessMapper iIllnessMapper;
    private final IItemRepository iItemRepository;
    private final IIllnessDetailMapper illnessDetailMapper;


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
    public IllnessEntity getIllnessById(Long id) {
        return illnessRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This illness is not existed!"));
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
            throw new AppException(HttpStatus.BAD_REQUEST, "No further update");
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
        return this.createIllness(illness);
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
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This illness is not existed!"));
    }

    @Override
    public IllnessEntity createIllness(IllnessCreateRequest request) {
        IllnessEntity illness = iIllnessMapper.toModel(request);

        // Ensure relations are set properly
        illness.setCowEntity(findCowEntity(request.getCowId()));
        illness.setUserEntity(UserStatic.getCurrentUser());
        illness.setIllnessStatus(IllnessStatus.pending);
        illness.setStartDate(LocalDate.now());

        if (request.getDetail() != null) {
//            illness.getIllnessDetails().forEach(detail -> {
//                detail.setIllnessEntity(illness);
//                detail.setStatus(IllnessDetailStatus.pending);
//                Long id = detail.getVaccine().getItemId();
//                var itemEntity = iItemRepository.findById(id)
//                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no item have id" + id));
//                detail.setVaccine(itemEntity);
//            });
            List<IllnessDetailEntity > illnessDetails = new ArrayList<>();
            request.getDetail().forEach(detail -> {
                IllnessDetailEntity illnessDetail = illnessDetailMapper.toModel(detail);
                illnessDetail.setStatus(IllnessDetailStatus.pending);
                illnessDetail.setIllnessEntity(illness);

                Long id = detail.getVaccineId();
                var itemEntity = iItemRepository.findById(id)
                        .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no item have id" + id));
                illnessDetail.setVaccine(itemEntity);

                illnessDetails.add(illnessDetail);
            });

            illness.setIllnessDetails(illnessDetails);
        }

        return illnessRepository.save(illness);
    }




    //-----------------------------------------------------
    private CowEntity findCowEntity(Long cowId){
        CowEntity cowEntity = cowRepository.findById(cowId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "This cow is not existed!"));
        return cowEntity;
    }


}
