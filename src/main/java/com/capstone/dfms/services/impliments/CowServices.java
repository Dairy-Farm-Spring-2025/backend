package com.capstone.dfms.services.impliments;

import com.capstone.dfms.components.exceptions.AppException;
import com.capstone.dfms.components.utils.QRCodeUtil;
import com.capstone.dfms.components.utils.StringUtils;
import com.capstone.dfms.mappers.ICowMapper;
import com.capstone.dfms.models.AreaEntity;
import com.capstone.dfms.models.CowEntity;
import com.capstone.dfms.models.CowTypeEntity;
import com.capstone.dfms.repositories.ICowRepository;
import com.capstone.dfms.repositories.ICowTypeRepository;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.responses.CowResponse;
import com.capstone.dfms.services.ICowServices;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

        request.setName(this.getInitials(cowType.getName()));

        CowEntity savedEntity = cowRepository.save(request);
        return cowMapper.toResponse(savedEntity);
    }

    @Override
    public CowResponse updateCow(Long id, CowUpdateRequest request) {
        CowEntity existingEntity = cowRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow with ID '" + id + "' not found."));

        cowMapper.updateCowFromRequest(request, existingEntity);

        if(request.getCowTypeId() != null){
            CowTypeEntity cowType = cowTypeRepository.findById(request.getCowTypeId())
                    .orElseThrow(() -> new AppException(HttpStatus.OK, "Cow type not found."));
            existingEntity.setCowTypeEntity(cowType);
        }

        CowEntity updatedEntity = cowRepository.save(existingEntity);
        return getCowById(updatedEntity.getCowId());
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

        CowResponse response = cowMapper.toResponse(cowEntity);

        // Check if the cow is in a pen and set the `isInPen` property
        boolean isInPen = this.cowIsInPen(cowEntity.getCowId());
        response.setInPen(isInPen);

        return response;
    }

    @Override
    public List<CowResponse> getAllCows() {
        List<CowEntity> cowEntities = cowRepository.findAll();
        return cowEntities.stream()
                .map(cowEntity -> {
                    // Map CowEntity to CowResponse
                    CowResponse response = cowMapper.toResponse(cowEntity);

                    // Check if the cow is in a pen
                    boolean isInPen = this.cowIsInPen(cowEntity.getCowId());
                    response.setInPen(isInPen);

                    return response;
                })
                .toList();
    }

    //----------General Function---------------------------------------------------
    private String generateCowName() {
        long count = cowRepository.count();

        return "CO" + String.format("%03d", count + 1);
    }

    public String getInitials(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Split the string into words
        String[] words = input.trim().split("\\s+");

        // Extract the first character of each word
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0)); // Append the first character
            }
        }

        return initials.toString() + (cowRepository.countByNameContains(initials.toString()) + 1);
    }

    private boolean cowIsInPen(Long cowId){
        return !cowRepository.isCowNotInAnyPen(cowId, LocalDate.now());
    }


    //--------------------------------------------------------------------------------
    private <T> void updateField(Supplier<T> getter, Consumer<T> setter) {
        T value = getter.get();
        if (value != null) {
            setter.accept(value);
        }
    }


    @Override
    public byte[] generateCowQRCode(Long cowId) {
        CowEntity cow = cowRepository.findById(cowId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Cow not found"));

        String cowUrl = "http://localhost:5173/dairy/cow-management/" + cowId;
        try {
            return QRCodeUtil.generateQRCode(cowUrl, 300, 300);
        } catch (Exception e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }
}
