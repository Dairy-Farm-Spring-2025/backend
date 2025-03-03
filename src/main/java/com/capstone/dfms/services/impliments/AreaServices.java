package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.IAreaMapper;
import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.repositories.IAreaRepository;
import com.capstone.dfms.repositories.IPenRepository;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.services.IAreaServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AreaServices implements IAreaServices {
    private final IAreaRepository areaRepository;
    private final IAreaMapper areaMapper;
    private final IPenRepository penRepository;


    @Override
    public AreaResponse createArea(AreaEntity request) {
        request.setName(StringUtils.NameStandardlizing(request.getName()));
        if (areaRepository.existsByName(request.getName())) {
            throw new AppException(HttpStatus.OK, "Area with the name '" + request.getName() + "' already exists.");
        }

        this.validateDimensions(request);

        float areaTotalSize = request.getLength() * request.getWidth();

        float penTotalSize = request.getMaxPen() * (request.getPenLength() * request.getPenWidth());

        if (penTotalSize >  areaTotalSize){
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Diện tích chuồng không phù hợp");
        }

        AreaEntity savedArea = areaRepository.save(request);

        int existingPenCount = penRepository.countByAreaBelongto(savedArea);

        List<PenEntity> pens = new ArrayList<>();
        int numberInRow = request.getNumberInRow() != null ? request.getNumberInRow() : 1;
        char rowLetter = 'A';

        for (int i = 1; i <= request.getMaxPen(); i++) {
            int penNumber = ((i - 1) % numberInRow) + 1;
            if (penNumber == 1 && i > 1) {
                rowLetter++;
            }
            String penName = rowLetter + String.format("%02d", penNumber);

            PenEntity pen = PenEntity.builder()
                    .name(penName)
                    .description("Automatically generated pen " + i)
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

        areaMapper.updateAreaFromRequest(request, existingEntity);

        this.validateDimensions(existingEntity);
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

        long occupied = penRepository.countPensByStatus(id, PenStatus.occupied);
        long empty = penRepository.countPensByStatus(id, PenStatus.empty);
        long underMaintenance = penRepository.countPensByStatus(id, PenStatus.underMaintenance);
        AreaResponse response = IAreaMapper.INSTANCE.toResponse(areaEntity);
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

            AreaResponse response = IAreaMapper.INSTANCE.toResponse(area);
            response.setOccupiedPens(occupied);
            response.setEmptyPens(empty);
            response.setDamagedPens(underMaintenance);
            return response;
        }).toList();
    }

    //----------------------------------------------------------------
    private void validateDimensions(AreaEntity areaEntity) {
        List<String> errorMessages = new ArrayList<>();

        // Validate positive numbers
        if (areaEntity.getWidth() <= 0) {
            errorMessages.add("Width must be a positive number.");
        }
        if (areaEntity.getLength() <= 0) {
            errorMessages.add("Length must be a positive number.");
        }
        if (areaEntity.getPenWidth() <= 0) {
            errorMessages.add("Width of pen must be a positive number.");
        }
        if (areaEntity.getPenLength() <= 0) {
            errorMessages.add("Length of pen must be a positive number.");
        }

        // Validate if area width is smaller than or equal to area length
        if (areaEntity.getWidth() > areaEntity.getLength()) {
            errorMessages.add("Width must be smaller than or equal to Length.");
        }

        // Validate if pen width is smaller than or equal to pen length
        if (areaEntity.getPenWidth() > areaEntity.getPenLength()) {
            errorMessages.add("Width of pen must be smaller than or equal to Length of pen.");
        }

        // If there are any error messages, throw an exception with the list of messages
        if (!errorMessages.isEmpty()) {
            throw new AppException(HttpStatus.OK, "Request không hợp lệ", errorMessages);
        }
    }


}
