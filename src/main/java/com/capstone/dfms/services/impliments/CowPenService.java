package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.mappers.ICowPenMapper;
import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.CowPenEntity;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.compositeKeys.CowPenPK;
import com.capstone.dfms.models.enums.PenCowStatus;
import com.capstone.dfms.models.enums.PenStatus;
import com.capstone.dfms.repositories.ICowPenRepository;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.IPenRepository;
import com.capstone.dfms.requests.CowPenBulkRequest;
import com.capstone.dfms.requests.CowPenMovingRequest;
import com.capstone.dfms.responses.CowPenBulkResponse;
import com.capstone.dfms.responses.CowPenResponse;
import com.capstone.dfms.services.ICowPenService;
import com.capstone.dfms.services.IPenServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (cowPenEntity.isPresent()) {
            throw new AppException(HttpStatus.OK, LocalizationUtils.getMessage("cow.pen.created.before"));
        }

        CowEntity cowEntity = cowRepository.findById(request.getId().getCowId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.not.found")));
        PenEntity penEntity = penRepository.findById(request.getId().getPenId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("pen.not.found")));

        List<CowPenEntity> existingCowPenForCow = cowPenRepository.findValidCowPensByCowId(cowEntity.getCowId(), LocalDate.now());
        for (var cowPen : existingCowPenForCow) {
            LocalDateTime toDate = cowPen.getToDate();
            if (toDate == null || request.getId().getFromDate().isAfter(toDate)) {
                throw new AppException(HttpStatus.OK, LocalizationUtils.getMessage("cow.in.another.pen"));
            }
        }

        List<CowPenEntity> existingCowPenForPen = cowPenRepository.findValidCowPensByPenId(penEntity.getPenId(), LocalDate.now());
        for (var cowPen : existingCowPenForPen) {
            LocalDateTime toDate = cowPen.getToDate();
            if (toDate == null || request.getId().getFromDate().isAfter(toDate)) {
                throw new AppException(HttpStatus.OK, LocalizationUtils.getMessage("pen.not.available"));
            }
        }

        request.setCowEntity(cowEntity);
        request.setPenEntity(penEntity);
        request.setStatus(PenCowStatus.planning);

        penEntity.setPenStatus(PenStatus.occupied);
        penRepository.save(penEntity);

        CowPenEntity savedEntity = cowPenRepository.save(request);
        return mapper.toResponse(savedEntity);
    }

    @Override
    public CowPenResponse update(Long penId, Long cowId, LocalDateTime fromDate, CowPenEntity updatedRequest) {
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
    public CowPenResponse getById(Long penId, Long cowId, LocalDateTime fromDate) {
        CowPenPK cowPenPK = new CowPenPK(penId, cowId, fromDate);

        CowPenEntity cowPenEntity = cowPenRepository.findById(cowPenPK)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow-Pen not found for the provided key."));

        return mapper.toResponse(cowPenEntity);
    }

    @Override
    public void delete(Long penId, Long cowId, LocalDateTime fromDate) {
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


    @Override
    public CowPenResponse approveOrRejectMovePen(Long penId, Long cowId, LocalDateTime fromDate, boolean isApproval) {
        CowPenPK cowPenPK = new CowPenPK(penId, cowId, fromDate);
        CowPenEntity cowPenEntity = cowPenRepository.findById(cowPenPK)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow-Pen not found for the provided key."));

        if(cowPenEntity.getStatus() != PenCowStatus.planning){
            throw new AppException(HttpStatus.BAD_REQUEST, "Not longer to approve or reject");
        }

        if(isApproval){
            cowPenEntity.setStatus(PenCowStatus.assigned);
        }
        else {
            cowPenEntity.setStatus(PenCowStatus.cancel);
            CowPenEntity oldCowPenEntity = cowPenRepository.findPreviousCowPensByCowId(cowId);
            oldCowPenEntity.setToDate(null);
            this.update(oldCowPenEntity.getId().getPenId(),
                    oldCowPenEntity.getId().getCowId(),
                    oldCowPenEntity.getId().getFromDate(),
                    oldCowPenEntity);
        }
        return this.update(penId, cowId, fromDate, cowPenEntity);
    }

    @Override
    public CowPenBulkResponse<CowPenResponse> createBulkCowPen(CowPenBulkRequest cowPenBulkRequest) {
        List<Long> cowEntities = cowPenBulkRequest.getCowEntities();
        List<Long> penEntities = cowPenBulkRequest.getPenEntities();
        LocalDateTime fromDate = LocalDateTime.now();

        if (cowEntities == null || penEntities == null || cowEntities.isEmpty() || penEntities.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST,"Cow entities and pen entities cannot be null or empty.");
        }

        if (cowEntities.size() != penEntities.size()) {
            throw new AppException(HttpStatus.BAD_REQUEST,"The number of cows and pens must match.");
        }
        List<CowPenResponse> responses = new ArrayList<>();
        ArrayList<String> errorList = new ArrayList<>();


        for (int i = 0; i < cowEntities.size(); i++) {
            int finalI = i;
            CowEntity cow = cowRepository.findById(cowEntities.get(finalI))
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Cow not found with ID: " + cowEntities.get(finalI)));
            PenEntity pen = penRepository.findById(penEntities.get(finalI))
                    .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Pen not found with ID: " + penEntities.get(finalI)));

            Optional<CowPenEntity> latestCowPen = cowPenRepository.findLatestCowPenByCowId(cow.getCowId());

            if (latestCowPen.isPresent() && latestCowPen.get().getToDate() == null) {
                String cowName = cow.getName();
                throw new AppException(HttpStatus.BAD_REQUEST, cowName + " is already in pen");
            }

            if (pen.getPenStatus() == PenStatus.occupied) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Pen '" + pen.getName() + "' is already occupied!");
            }

            AreaEntity area = pen.getAreaBelongto();
            if (!cow.getCowTypeEntity().getCowTypeId().equals(area.getCowTypeEntity().getCowTypeId())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.invalid_type_for_area"));
            }
            if (!cow.getCowStatus().equals(area.getCowStatus())) {
                throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.invalid_status_for_area"));
            }

            CowPenPK cowPenPK = new CowPenPK(pen.getPenId(), cow.getCowId(), fromDate);
            CowPenEntity cowPenEntity = new CowPenEntity();
            cowPenEntity.setId(cowPenPK);
            cowPenEntity.setCowEntity(cow);
            cowPenEntity.setPenEntity(pen);
            cowPenEntity.setStatus(PenCowStatus.inPen);
            cowPenRepository.save(cowPenEntity);
            CowPenResponse response = mapper.toResponse(cowPenEntity);
            responses.add(response);
            pen.setPenStatus(PenStatus.occupied);
            penRepository.save(pen);
        }



        return new CowPenBulkResponse<>(responses, errorList);
    }

    @Override
    public CowPenResponse movingPen(CowPenMovingRequest request) {
        if(request.getOldCowPen().getCowId() != request.getNewCowPen().getCowId())
            throw new AppException(HttpStatus.BAD_REQUEST, "Cow is the same!");

        CowPenEntity oldCowPenEntity = cowPenRepository.findById(request.getOldCowPen())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Cow-Pen not found for the provided key."));

        oldCowPenEntity.setToDate(LocalDateTime.now());
        this.update(oldCowPenEntity.getId().getPenId(),
                oldCowPenEntity.getId().getCowId(),
                oldCowPenEntity.getId().getFromDate(),
                oldCowPenEntity);

        CowPenEntity newCowPenEntity = mapper.toModel(request.getNewCowPen());
        CowPenResponse newCowPenResponse = this.create(newCowPenEntity);
        return newCowPenResponse;
    }

    private String validateCowPen(CowEntity cow, PenEntity pen, LocalDate fromDate) {
        StringBuilder error = new StringBuilder();

        if (!cowPenRepository.findValidCowPensByCowId(cow.getCowId(), fromDate).isEmpty()) {
            error.append("Cow ").append(cow.getCowId()).append(" is already in another pen. ");
        }

        if (!cowPenRepository.findValidCowPensByPenId(pen.getPenId(), fromDate).isEmpty()) {
            error.append("Pen ").append(pen.getPenId()).append(" is already occupied. ");
        }

        return error.toString();
    }

    @Override
    public CowPenResponse createCowPen(CowPenEntity request) {
        Optional<CowPenEntity> cowPenEntity = cowPenRepository.findById(request.getId());
        if (cowPenEntity.isPresent()) {
            throw new AppException(HttpStatus.OK, LocalizationUtils.getMessage("cow.pen.created.before"));
        }
        CowEntity cowEntity = cowRepository.findById(request.getId().getCowId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Cow not found with ID: " + request.getId().getCowId()));
        PenEntity penEntity = penRepository.findById(request.getId().getPenId())
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Pen not found with ID: " + request.getId().getPenId()));
        AreaEntity area = penEntity.getAreaBelongto();
        if (!cowEntity.getCowTypeEntity().getCowTypeId().equals(area.getCowTypeEntity().getCowTypeId())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.invalid_type_for_area"));
        }
        if (!cowEntity.getCowStatus().equals(area.getCowStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST, LocalizationUtils.getMessage("cow.invalid_status_for_area"));
        }

        Optional<CowPenEntity> latestCowPen = cowPenRepository.findLatestCowPenByCowId(cowEntity.getCowId());
        PenEntity oldPen = null;

        if (latestCowPen.isPresent()) {
            CowPenEntity cowPen = latestCowPen.get();
            if (cowPen.getToDate() == null) {
                cowPen.setStatus(PenCowStatus.finished);
                cowPen.setToDate(LocalDateTime.now());
                cowPenRepository.save(cowPen);
            }
            oldPen = cowPen.getPenEntity();
            oldPen.setPenStatus(PenStatus.empty);
            penRepository.save(oldPen);
        }

        Optional<CowPenEntity> existingCowInNewPen = cowPenRepository.findCurrentCowInPen(penEntity.getPenId());
        boolean isWaiting = existingCowInNewPen.isPresent();

        request.setStatus(isWaiting ? PenCowStatus.waiting : PenCowStatus.inPen);
        penEntity.setPenStatus(PenStatus.occupied);

        if (oldPen != null) {
            Optional<CowPenEntity> waitingCow = cowPenRepository.findFirstWaitingCowInPen(oldPen.getPenId());
            if (waitingCow.isPresent()) {
                CowPenEntity cowToEnter = waitingCow.get();
                cowToEnter.setStatus(PenCowStatus.inPen);
                cowPenRepository.save(cowToEnter);
            } else {
                oldPen.setPenStatus(PenStatus.empty);
                penRepository.save(oldPen);
            }
        }

        request.setCowEntity(cowEntity);
        request.setPenEntity(penEntity);
        request.getId().setFromDate(LocalDateTime.now());
        penEntity.setPenStatus(PenStatus.occupied);
        penRepository.save(penEntity);
        CowPenEntity savedEntity = cowPenRepository.save(request);

        String message = isWaiting
                ? LocalizationUtils.getMessage("cow.must_wait_before_entering_pen")
                : LocalizationUtils.getMessage("cow.enter_pen_success");
        CowPenResponse response = mapper.toResponse(savedEntity);
        response.setMessage(message);

        return response;
    }

}
