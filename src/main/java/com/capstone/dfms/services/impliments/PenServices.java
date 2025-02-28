package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IPenMapper;
import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.repositories.IAreaRepository;
import com.capstone.dfms.repositories.IPenRepository;
import com.capstone.dfms.requests.PenUpdateRequest;
import com.capstone.dfms.responses.PenResponse;
import com.capstone.dfms.responses.PenStatusCountResponse;
import com.capstone.dfms.services.IPenServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class PenServices implements IPenServices {
    private final IPenRepository penRepository;
    private final IAreaRepository areaRepository;
    private final IPenMapper penMapper;

    @Override
    public PenResponse createPen(PenEntity request) {
        if (penRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.OK, "Pen with the name '" + request.getName() + "' already exists.");
        }
        AreaEntity area = areaRepository.findById(request.getAreaBelongto().getAreaId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Area not found."));
        request.setAreaBelongto(area);
        PenEntity savedPen = penRepository.save(request);
        return penMapper.toResponse(savedPen);
    }

    @Override
    public PenResponse updatePen(Long id, PenUpdateRequest request) {
        PenEntity existingPen = penRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Pen with ID '" + id + "' not found."));

        if(request.getName() != null) {
            request.setName(StringUtils.NameStandardlizing(request.getName()));
            if (penRepository.existsByName(request.getName())
                    && !existingPen.getName().equalsIgnoreCase(request.getName())) {
                throw new AppException(HttpStatus.OK, "Area with the name '" + request.getName() + "' already exists.");
            }
        }

        penMapper.updatePenFromRequest(request, existingPen);

//        existingPen.setName(request.getName() != null ? request.getName() : existingPen.getName());
//        existingPen.setDescription(request.getDescription() != null ? request.getDescription() : existingPen.getDescription());
//        existingPen.setPenType(request.getPenType() != null ? request.getPenType() : existingPen.getPenType());
//        existingPen.setLength(request.getLength() != 0 ? request.getLength() : existingPen.getLength());
//        existingPen.setWidth(request.getWidth() != 0 ? request.getWidth() : existingPen.getWidth());
//        existingPen.setPenStatus(request.getPenStatus() != null ? request.getPenStatus() : existingPen.getPenStatus());

        if (request.getAreaId() != null) {
            AreaEntity area = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new AppException(HttpStatus.OK, "Area not found."));
            existingPen.setAreaBelongto(area);
        }

        PenEntity updatedPen = penRepository.save(existingPen);
        return penMapper.toResponse(updatedPen);
    }

    @Override
    public void deletePen(Long id) {
        PenEntity existingPen = penRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Pen with ID '" + id + "' not found."));
        penRepository.delete(existingPen);
    }

    @Override
    public PenResponse getPenById(Long id) {
        PenEntity penEntity = penRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Pen with ID '" + id + "' not found."));
        return penMapper.toResponse(penEntity);
    }

    @Override
    public List<PenResponse> getAllPens() {
        List<PenEntity> penEntities = penRepository.findAll();
        return penEntities.stream().map(penMapper::toResponse).toList();
    }

    @Override
    public List<PenEntity> getAvailablePens(LocalDate currentDate) {
        return penRepository.findAvailablePens(currentDate);
    }

    @Override
    public List<PenEntity> getPenByArea(Long areaId) {
        return penRepository.findByArea(areaId);
    }

    @Override
    public PenStatusCountResponse getPenStatusCountByArea(Long areaId) {
        long occupied = penRepository.countPensByStatus(areaId, PenStatus.occupied);
        long empty = penRepository.countPensByStatus(areaId, PenStatus.empty);
        long underMaintenance = penRepository.countPensByStatus(areaId, PenStatus.underMaintenance);

        return new PenStatusCountResponse(occupied, empty, underMaintenance);
    }


}
