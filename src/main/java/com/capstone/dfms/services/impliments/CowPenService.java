package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.mappers.ICowPenMapper;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.CowPenEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.compositeKeys.CowPenPK;
import com.capstone.dfms.models.enums.PenCowStatus;
import com.capstone.dfms.repositories.ICowPenRepository;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IPenRepository;
import com.capstone.dfms.responses.CowPenResponse;
import com.capstone.dfms.services.ICowPenService;
import com.capstone.dfms.services.IPenServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CowPenService implements ICowPenService {
    private final ICowPenRepository cowPenRepository;
    private final ICowRepository cowRepository;
    private final IPenRepository penRepository;
    private final ICowPenMapper mapper;

    @Override
    public CowPenResponse create(CowPenEntity request) {
        Optional<CowPenEntity> cowPenEntity = cowPenRepository.findById(request.getId());
        if(cowPenEntity.isPresent()){
            throw new AppException(HttpStatus.OK, "Cow-Pen have created before");
        }

        CowEntity cowEntity = cowRepository.findById(request.getId().getCowId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow not found with ID: " + request.getId().getCowId()));
        PenEntity penEntity = penRepository.findById(request.getId().getPenId())
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Pen not found with ID: " + request.getId().getPenId()));

        List<CowPenEntity> existingCowPenForCow = cowPenRepository.findValidCowPensByCowId(cowEntity.getCowId(), LocalDate.now());
        if(existingCowPenForCow.size() != 0)
            throw new AppException(HttpStatus.OK, "Cow in another Pen!");
        List<CowPenEntity> existingCowPenForPen = cowPenRepository.findValidCowPensByPenId(penEntity.getPenId(), LocalDate.now());
        if(existingCowPenForPen.size() != 0)
            throw new AppException(HttpStatus.OK, "Pen is not available!");

        // Assign found entities to the request
        request.setCowEntity(cowEntity);
        request.setPenEntity(penEntity);
        request.setStatus(PenCowStatus.planning);

        CowPenEntity savedEntity = cowPenRepository.save(request);
        return mapper.toResponse(savedEntity);
    }

    @Override
    public CowPenResponse update(Long penId, Long cowId, LocalDate fromDate, CowPenEntity updatedRequest) {
        CowPenPK cowPenPK = new CowPenPK(penId, cowId, fromDate);

        CowPenEntity existingEntity = cowPenRepository.findById(cowPenPK)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow-Pen not found for the provided key."));

        // Update the entity fields
        existingEntity.setToDate(updatedRequest.getToDate() != null ? updatedRequest.getToDate() : existingEntity.getToDate());
        existingEntity.setStatus(updatedRequest.getStatus() != null ? updatedRequest.getStatus() : existingEntity.getStatus());

        CowPenEntity savedEntity = cowPenRepository.save(existingEntity);

        return mapper.toResponse(savedEntity);
    }

    @Override
    public List<CowPenResponse> getAll() {
        return cowPenRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CowPenResponse getById(Long penId, Long cowId, LocalDate fromDate) {
        // Construct the composite key
        CowPenPK cowPenPK = new CowPenPK(penId, cowId, fromDate);

        // Find the entity by composite key
        CowPenEntity cowPenEntity = cowPenRepository.findById(cowPenPK)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow-Pen not found for the provided key."));

        // Convert to response and return
        return mapper.toResponse(cowPenEntity);
    }

    @Override
    public void delete(Long penId, Long cowId, LocalDate fromDate) {
        CowPenPK cowPenPK = new CowPenPK(penId, cowId, fromDate);

        // Check if the entity exists and delete it
        if (cowPenRepository.existsById(cowPenPK)) {
            cowPenRepository.deleteById(cowPenPK);
        } else {
            throw new AppException(HttpStatus.OK, "Cow-Pen not found for the provided key.");
        }
    }

    @Override
    public List<CowPenResponse> getCowPenFollowCowId(Long cowId) {
        return cowPenRepository.findByIdCowId(cowId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CowPenResponse> getCowPenFollowPenId(Long penId) {
        return cowPenRepository.findByIdPenId(penId).stream()
                .map(mapper::toResponse)
                .toList();
    }


    //----------------------------------------------------------
    @Override
    public CowPenResponse approveOrRejectMovePen(Long penId, Long cowId, LocalDate fromDate, boolean isApproval) {
        CowPenPK cowPenPK = new CowPenPK(penId, cowId, fromDate);
        CowPenEntity cowPenEntity = cowPenRepository.findById(cowPenPK)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow-Pen not found for the provided key."));

        if(cowPenEntity.getStatus() != PenCowStatus.planning){
            throw new AppException(HttpStatus.OK, "Not longer to approve or reject");
        }

        if(isApproval){
            cowPenEntity.setStatus(PenCowStatus.assigned);
        }
        else {
            cowPenEntity.setStatus(PenCowStatus.cancel);
        }
        return this.update(penId, cowId, fromDate, cowPenEntity);
    }

}
