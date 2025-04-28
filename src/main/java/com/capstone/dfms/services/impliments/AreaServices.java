package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IAreaMapper;
import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.PenCowStatus;
import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.repositories.IAreaRepository;
import com.capstone.dfms.repositories.ICowPenRepository;
import com.capstone.dfms.repositories.ICowTypeRepository;
import com.capstone.dfms.repositories.IPenRepository;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.services.IAreaServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class AreaServices implements IAreaServices {
    private final IAreaRepository areaRepository;
    private final IAreaMapper areaMapper;
    private final IPenRepository penRepository;
    private final ICowTypeRepository cowTypeRepository;
    private final ICowPenRepository cowPenRepository;


    @Override
    public AreaResponse createArea(AreaEntity request) {
        request.setName(StringUtils.NameStandardlizing(request.getName()));

        if (areaRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("area.exists"));
        }

        this.validateDimensions(request);

        float areaTotalSize = request.getLength() * request.getWidth();
        float penTotalSize = request.getMaxPen() * (request.getPenLength() * request.getPenWidth());

        if (penTotalSize > areaTotalSize) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("area.invalid_size"));
        }

        if (request.getCowTypeEntity() != null && request.getCowTypeEntity().getCowTypeId() != null) {
            CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeEntity().getCowTypeId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cow type not found."));
            request.setCowTypeEntity(cowType);
        } else {
            request.setCowTypeEntity(null);
        }

        AreaEntity savedArea = areaRepository.save(request);

        List<PenEntity> pens = new ArrayList<>();
        int numberInRow = request.getNumberInRow() != null ? request.getNumberInRow() : 1;
        char rowLetter = 'A';

        String areaName = savedArea.getName();
        for (int i = 1; i <= request.getMaxPen(); i++) {
            int penNumber = ((i - 1) % numberInRow) + 1;
            if (penNumber == 1 && i > 1) {
                rowLetter++;
            }
            String penName = rowLetter + String.format("%02d", penNumber);

            PenEntity pen = PenEntity.builder()
                    .name(penName)
                    .description(areaName + " - " + penName)
                    .penStatus(PenStatus.empty)
                    .areaBelongto(savedArea)
                    .build();
            pens.add(pen);
        }
        penRepository.saveAll(pens);

        return areaMapper.INSTANCE.toResponse(savedArea);
    }


    @Override
    public AreaResponse updateArea(Long id, AreaUpdateRequest request) {
        AreaEntity existingEntity = areaRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("area.not_found")));

        if (request.getName() != null) {
            request.setName(StringUtils.NameStandardlizing(request.getName()));
            if (areaRepository.existsByName(request.getName())
                    && !existingEntity.getName().equalsIgnoreCase(request.getName())) {
                throw new AppException(HttpStatus.OK,
                        LocalizationUtils.getMessage("area.exists"));
            }
        }
        boolean hasCows = cowPenRepository.existsByPenEntityAreaBelongtoAndStatus(
                existingEntity, PenCowStatus.inPen);
        if (hasCows) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Không để cập nhật loại bò và trạng thái vì khu vực đang chứa bò");
        }

        if (request.getCowTypeId() != null) {
            CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cow type not found."));
            existingEntity.setCowTypeEntity(cowType);
        }


        areaMapper.updateAreaFromRequest(request, existingEntity);
        validateDimensions(existingEntity);

        AreaEntity updatedEntity = areaRepository.save(existingEntity);
        return areaMapper.toResponse(updatedEntity);
    }

    @Override
    public void deleteArea(Long id) {
        AreaEntity existingEntity = areaRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK,
                        LocalizationUtils.getMessage("area.not_found")));
        areaRepository.delete(existingEntity);
    }

    @Override
    public AreaResponse getAreaById(Long id) {
        AreaEntity areaEntity = areaRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST,
                        LocalizationUtils.getMessage("area.not_found")));

        long occupied = penRepository.countPensByStatus(id, PenStatus.occupied);
        long empty = penRepository.countPensByStatus(id, PenStatus.empty);
        long underMaintenance = penRepository.countPensByStatus(id, PenStatus.underMaintenance);

        AreaResponse response = areaMapper.toResponse(areaEntity);
        response.setOccupiedPens(occupied);
        response.setEmptyPens(empty);
        response.setDamagedPens(underMaintenance);
        return response;
    }

    @Override
    public List<AreaResponse> getAllAreas() {
        List<AreaEntity> areaEntities = areaRepository.findAll();

        return areaEntities.stream().map(area -> {
            long occupied = penRepository.countPensByStatus(area.getAreaId(), PenStatus.occupied);
            long empty = penRepository.countPensByStatus(area.getAreaId(), PenStatus.empty);
            long underMaintenance = penRepository.countPensByStatus(area.getAreaId(), PenStatus.underMaintenance);

            AreaResponse response = areaMapper.toResponse(area);
            response.setOccupiedPens(occupied);
            response.setEmptyPens(empty);
            response.setDamagedPens(underMaintenance);
            return response;
        }).toList();
    }

    //----------------------------------------------------------------
    private void validateDimensions(AreaEntity areaEntity) {
        List<String> errorMessages = new ArrayList<>();

        if (areaEntity.getWidth() <= 0) {
            errorMessages.add(LocalizationUtils.getMessage("validation.positive.width"));
        }
        if (areaEntity.getLength() <= 0) {
            errorMessages.add(LocalizationUtils.getMessage("validation.positive.length"));
        }
        if (areaEntity.getPenWidth() <= 0) {
            errorMessages.add(LocalizationUtils.getMessage("validation.positive.pen_width"));
        }
        if (areaEntity.getPenLength() <= 0) {
            errorMessages.add(LocalizationUtils.getMessage("validation.positive.pen_length"));
        }
        if (areaEntity.getWidth() > areaEntity.getLength()) {
            errorMessages.add(LocalizationUtils.getMessage("validation.dimension.width_smaller_than_length"));
        }
        if (areaEntity.getPenWidth() > areaEntity.getPenLength()) {
            errorMessages.add(LocalizationUtils.getMessage("validation.dimension.pen_width_smaller_than_pen_length"));
        }

        if (!errorMessages.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    LocalizationUtils.getMessage("validation.invalid_request"),
                    errorMessages);
        }
    }


}
