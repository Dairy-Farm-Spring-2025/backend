package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.ICowMapper;
import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.ICowTypeRepository;
import com.capstone.dfms.responses.CowResponse;
import com.capstone.dfms.services.ICowServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class CowServices implements ICowServices {
    private final ICowRepository cowRepository;
    private final ICowTypeRepository cowTypeRepository;
    private final ICowMapper cowMapper;


    @Override
    public CowResponse createCow(CowEntity request) {
        if (cowRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.OK, "Cow with the name '" + request.getName() + "' already exists.");
        }

        CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeEntity().getCowTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow type not found."));
        request.setCowTypeEntity(cowType);

        CowEntity savedEntity = cowRepository.save(request);
        return cowMapper.toResponse(savedEntity);
    }

    @Override
    public CowResponse updateCow(Long id, CowEntity request) {
        CowEntity existingEntity = cowRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow with ID '" + id + "' not found."));

        if(request.getName() != null) {
            request.setName(StringUtils.NameStandardlizing(request.getName()));
            if (cowRepository.existsByName(request.getName())
                    && !existingEntity.getName().equalsIgnoreCase(request.getName())) {
                throw new AppException(HttpStatus.OK, "Area with the name '" + request.getName() + "' already exists.");
            }
        }

        existingEntity.setName(request.getName() != null ? request.getName() : existingEntity.getName());
        existingEntity.setCowStatus(request.getCowStatus() != null ? request.getCowStatus() : existingEntity.getCowStatus());
        existingEntity.setDateOfBirth(request.getDateOfBirth() != null ? request.getDateOfBirth() : existingEntity.getDateOfBirth());
        existingEntity.setDateOfEnter(request.getDateOfEnter() != null ? request.getDateOfEnter() : existingEntity.getDateOfEnter());
        existingEntity.setDateOfOut(request.getDateOfOut() != null ? request.getDateOfOut() : existingEntity.getDateOfOut());
        existingEntity.setDescription(request.getDescription() != null ? request.getDescription() : existingEntity.getDescription());
        existingEntity.setCowOrigin(request.getCowOrigin() != null ? request.getCowOrigin() : existingEntity.getCowOrigin());
        existingEntity.setGender(request.getGender() != null ? request.getGender() : existingEntity.getGender());

        CowEntity updatedEntity = cowRepository.save(existingEntity);
        return cowMapper.toResponse(updatedEntity);
    }

    @Override
    public void deleteCow(Long id) {
        CowEntity existingEntity = cowRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow with ID '" + id + "' not found."));
        cowRepository.delete(existingEntity);
    }

    @Override
    public CowResponse getCowById(Long id) {
        CowEntity cowEntity = cowRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow with ID '" + id + "' not found."));
        return cowMapper.toResponse(cowEntity);
    }

    @Override
    public List<CowResponse> getAllCows() {
        List<CowEntity> cowEntities = cowRepository.findAll();
        return cowEntities.stream()
                .map(cowMapper::toResponse)
                .toList();
    }

    //----------General Function---------------------------------------------------
    public LocalDate convertDateToLocalDate(Date date) {
        return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
}
