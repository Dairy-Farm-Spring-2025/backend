package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.ICowTypeMapper;
import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.repositories.ICowTypeRepository;
import com.capstone.dfms.responses.CowTypeResponse;
import com.capstone.dfms.services.ICowTypeServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CowTypeServices implements ICowTypeServices {
    private final ICowTypeRepository cowTypeRepository;
    private final ICowTypeMapper cowTypeMapper;

    @Override
    public CowTypeResponse createCowType(CowTypeEntity request) {
        // Check if a cow type with the same name exists
        request.setName(StringUtils.NameStandardlizing(request.getName()));
        if (cowTypeRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.OK, "Cow type with the name '" + request.getName() + "' already exists.");
        }

        // Save the new cow type and map the result to a response
        CowTypeEntity savedEntity = cowTypeRepository.save(request);
        return cowTypeMapper.toResponse(savedEntity);
    }

    @Override
    public CowTypeResponse updateCowType(Long id, CowTypeEntity request) {
        CowTypeEntity existingEntity = cowTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow type with ID '" + id + "' not found."));

        if(request.getName() != null) {
            request.setName(StringUtils.NameStandardlizing(request.getName()));
            if (cowTypeRepository.existsByName(request.getName())
                    && !existingEntity.getName().equalsIgnoreCase(request.getName())) {
                throw new AppException(HttpStatus.OK, "Area with the name '" + request.getName() + "' already exists.");
            }
        }

        // Update fields of the existing entity
        existingEntity.setName(request.getName() != null ? request.getName() : existingEntity.getName());
        existingEntity.setDescription(request.getDescription() != null ? request.getDescription() : existingEntity.getDescription());
        existingEntity.setStatus(request.getStatus() != null ? request.getStatus() : existingEntity.getStatus());

        // Save the updated entity
        CowTypeEntity updatedEntity = cowTypeRepository.save(existingEntity);

        // Map the updated entity to a response and return
        return cowTypeMapper.toResponse(updatedEntity);
    }

    @Override
    public void deleteCowType(Long id) {
        CowTypeEntity existingEntity = cowTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow type with ID '" + id + "' not found."));
        cowTypeRepository.delete(existingEntity);
    }

    @Override
    public CowTypeResponse getCowTypeById(Long id) {
        CowTypeEntity cowTypeEntity = cowTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow type with ID '" + id + "' not found."));
        return cowTypeMapper.toResponse(cowTypeEntity);
    }

    @Override
    public List<CowTypeResponse> getAllCowTypes() {
        List<CowTypeEntity> cowTypeEntities = cowTypeRepository.findAll();
        return cowTypeEntities.stream()
                .map(cowTypeMapper::toResponse)
                .toList();
    }
}
