package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.mappers.IIllnessDetailMapper;
import com.capstone.dfms.mappers.IIllnessMapper;
import com.capstone.dfms.models.IllnessDetailEntity;
import com.capstone.dfms.models.IllnessEntity;
import com.capstone.dfms.models.ItemEntity;
import com.capstone.dfms.models.UserEntity;
import com.capstone.dfms.repositories.IIllnessDetailRepository;
import com.capstone.dfms.repositories.IIllnessRepository;
import com.capstone.dfms.repositories.IItemRepository;
import com.capstone.dfms.repositories.IUserRepository;
import com.capstone.dfms.requests.IllnessDetailUpdateRequest;
import com.capstone.dfms.services.IIllnessDetailService;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    public IllnessDetailEntity createIllnessDetail(IllnessDetailEntity detail) {
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
            if(itemEntity.getCategoryEntity().getName().contains("Vaccine"))
                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
            detail.setVaccine(itemEntity);
        }

        if(detail.getVeterinarian().getId() != null){
            Long id = detail.getVeterinarian().getId();
            userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no user have id" + id));
            if(userEntity.getRoleId().getName().equalsIgnoreCase("VETERINARIANS"))
                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
            detail.setVeterinarian(userEntity);
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
        return illnessDetailRepository.findByIllnessEntityIllnessId(illnessId);
    }

    @Override
    public IllnessDetailEntity updateIllnessDetail(Long id, IllnessDetailUpdateRequest updatedDetail) {
        IllnessEntity illness = null;
        ItemEntity itemEntity = null;
        UserEntity userEntity = null;

        if(updatedDetail.getIllnessId() != null){
            Long tempId = updatedDetail.getIllnessId();
            illness = illnessRepository.findById(tempId)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no illness have id" + tempId));
        }

        if(updatedDetail.getItemId() != null){
            Long tempId = updatedDetail.getItemId();
            itemEntity = iItemRepository.findById(tempId)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no item have id" + tempId));
            if(itemEntity.getCategoryEntity().getName().contains("Vaccine"))
                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
        }

        if(updatedDetail.getVeterinarianId() != null){
            Long tempId = updatedDetail.getVeterinarianId();
            userEntity = userRepository.findById(tempId)
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "There are no user have id" + tempId));
            if(userEntity.getRoleId().getName().equalsIgnoreCase("VETERINARIANS"))
                throw new AppException(HttpStatus.BAD_REQUEST, "Item is not vaccine");
        }


        IllnessDetailEntity oldIllnessDetail = this.getIllnessDetailById(id);
        mapper.updateEntityFromDto(updatedDetail, oldIllnessDetail);

        illnessDetailRepository.save(oldIllnessDetail);
        return oldIllnessDetail;
    }

    @Override
    public void deleteIllnessDetail(Long id) {
        illnessDetailRepository.deleteById(id);
    }

    private void validate (@Nullable Long veterinarianId, @Nullable Long itemId, @Nullable Long illnessId, IllnessEntity illness, ItemEntity itemEntity, UserEntity userEntity){

    }
}
