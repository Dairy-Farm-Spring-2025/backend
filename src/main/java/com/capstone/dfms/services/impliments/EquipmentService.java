package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IEquipmentMapper;
import com.capstone.dfms.models.EquipmentEntity;
import com.capstone.dfms.repositories.IEquipmentRepository;
import com.capstone.dfms.requests.EquipmentRequest;
import com.capstone.dfms.services.IEquipmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EquipmentService implements IEquipmentService {
    private final IEquipmentRepository equipmentRepository;
    private final IEquipmentMapper equipmentMapper;

    @Override
    public EquipmentEntity createEquipment(EquipmentRequest request) {
        request.setName(StringUtils.NameStandardlizing(request.getName()));

        Optional<EquipmentEntity> entity = equipmentRepository.findEquipmentEntityByName(request.getName());
        if(entity.isPresent()){
            throw new AppException(HttpStatus.BAD_REQUEST, "Equipment is already existed!");
        }

        EquipmentEntity equipment = equipmentMapper.toModel(request);
        return equipmentRepository.save(equipment);
    }

    @Override
    public List<EquipmentEntity> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    @Override
    public EquipmentEntity getEquipmentById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
    }

    @Override
    public EquipmentEntity updateEquipment(Long id, EquipmentRequest request) {
        if (request.getName() != null){
            request.setName(StringUtils.NameStandardlizing(request.getName()));

            Optional<EquipmentEntity> entity = equipmentRepository.findEquipmentEntityByName(request.getName());
            if(entity.isPresent()){
                throw new AppException(HttpStatus.BAD_REQUEST, "Equipment is already existed!");
            }
        }

        EquipmentEntity existingEquipment = getEquipmentById(id);
        equipmentMapper.updateEntityFromDto(request, existingEquipment);
        return equipmentRepository.save(existingEquipment);
    }

    @Override
    public void deleteEquipment(Long id) {
        EquipmentEntity existingEquipment = getEquipmentById(id);
        equipmentRepository.delete(existingEquipment);
    }
}
