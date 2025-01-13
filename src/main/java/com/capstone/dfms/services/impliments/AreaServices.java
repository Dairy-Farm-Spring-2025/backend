package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IAreaMapper;
import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.repositories.IAreaRepository;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.services.IAreaServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AreaServices implements IAreaServices {
    private final IAreaRepository areaRepository;
    private final IAreaMapper areaMapper;

    @Override
    public AreaResponse createArea(AreaEntity request) {
        request.setName(StringUtils.NameStandardlizing(request.getName()));
        if (areaRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.OK, "Area with the name '" + request.getName() + "' already exists.");
        }

        // Save the new area and map the result to a response
        AreaEntity savedEntity = areaRepository.save(request);
        return areaMapper.INSTANCE.toResponse(savedEntity);
    }

    @Override
    public AreaResponse updateArea(Long id, AreaEntity request) {
        // Fetch the existing entity
        AreaEntity existingEntity = areaRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Area with ID '" + id + "' not found."));

        if(request.getName() != null) {
            request.setName(StringUtils.NameStandardlizing(request.getName()));
            if (areaRepository.existsByName(request.getName())
                    && !existingEntity.getName().equalsIgnoreCase(request.getName())) {
                throw new AppException(HttpStatus.OK, "Area with the name '" + request.getName() + "' already exists.");
            }
        }

        // Update fields using ternary operators
        existingEntity.setName(request.getName() != null ? request.getName() : existingEntity.getName());
        existingEntity.setDescription(request.getDescription() != null ? request.getDescription() : existingEntity.getDescription());
        existingEntity.setLength(request.getLength() != 0 ? request.getLength() : existingEntity.getLength());
        existingEntity.setWidth(request.getWidth() != 0 ? request.getWidth() : existingEntity.getWidth());
        existingEntity.setAreaType(request.getAreaType() != null ? request.getAreaType() : existingEntity.getAreaType());

        // Save the updated entity
        AreaEntity updatedEntity = areaRepository.save(existingEntity);

        // Map the updated entity to a response and return
        return areaMapper.toResponse(updatedEntity);
    }


    @Override
    public void deleteArea(Long id) {
        AreaEntity existingEntity = areaRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Area with ID '" + id + "' not found."));
        areaRepository.delete(existingEntity);
    }

    @Override
    public AreaResponse getAreaById(Long id) {
        AreaEntity areaEntity = areaRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Area with ID '" + id + "' not found."));
        return areaMapper.toResponse(areaEntity);
    }

    @Override
    public List<AreaResponse> getAllAreas() {
        List<AreaEntity> areaEntities = areaRepository.findAll();
        return areaEntities.stream()
                .map(areaMapper::toResponse)
                .toList();
    }
}
