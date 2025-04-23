package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.models.PenEntity;
import com.capstone.dfms.models.enums.AreaType;
import com.capstone.dfms.models.enums.CowStatus;
import com.capstone.dfms.requests.PenCreateRequest;
import com.capstone.dfms.requests.PenUpdateRequest;
import com.capstone.dfms.responses.PenResponse;
import com.capstone.dfms.responses.PenStatusCountResponse;
import com.capstone.dfms.services.IPenServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.capstone.dfms.mappers.IPenMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/pens")
@RequiredArgsConstructor
public class PenController {
    private final IPenServices penServices;
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<PenResponse> createPen(
            @Valid @RequestBody PenCreateRequest penCreateRequest
    ) {
        PenResponse penResponse = penServices.createPen(INSTANCE.toModel(penCreateRequest));
        return CoreApiResponse.success(penResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<PenResponse> updatePen(
            @PathVariable Long id,
            @Valid @RequestBody PenUpdateRequest penUpdateRequest
    ) {
        PenResponse penResponse = penServices.updatePen(id, penUpdateRequest);
        return CoreApiResponse.success(penResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/{id}")
    public CoreApiResponse<PenResponse> getPenById(@PathVariable Long id) {
        PenResponse penResponse = penServices.getPenById(id);
        return CoreApiResponse.success(penResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping
    public CoreApiResponse<List<PenResponse>> getAllPens() {
        List<PenResponse> pens = penServices.getAllPens();
        return CoreApiResponse.success(pens);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/available")
    public CoreApiResponse<List<PenEntity>> getAvailablePen() {
        // Get available pens
        List<PenEntity> availablePens = penServices.getAvailablePens(LocalDate.now());

        // Return the response
        return CoreApiResponse.success(availablePens);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/area/{id}")
    public CoreApiResponse<List<PenEntity>> getPenByArea(@PathVariable Long id) {
        return CoreApiResponse.success(penServices.getPenByArea(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/status-count/{areaId}")
    public CoreApiResponse<PenStatusCountResponse> getPenStatusCount(@PathVariable Long areaId) {
        PenStatusCountResponse result = penServices.getPenStatusCountByArea(areaId);
        return CoreApiResponse.success(result);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/available/cow")
    public CoreApiResponse<List<PenResponse>> getAvailablePens(
            @RequestParam(required = false) Long cowTypeId,
            @RequestParam(required = false) CowStatus cowStatus,
            @RequestParam AreaType areaType
    ) {
        List<PenResponse> pens = penServices.getPensByCowTypeAndStatus(cowTypeId, cowStatus, areaType);
        return CoreApiResponse.success(pens);
    }
}
