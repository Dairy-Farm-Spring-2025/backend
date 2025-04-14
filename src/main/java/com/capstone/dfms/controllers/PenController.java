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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.capstone.dfms.mappers.IPenMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/pens")
@RequiredArgsConstructor
public class PenController {
    private final IPenServices penServices;
    @PostMapping("/create")
    public CoreApiResponse<PenResponse> createPen(
            @Valid @RequestBody PenCreateRequest penCreateRequest
    ) {
        PenResponse penResponse = penServices.createPen(INSTANCE.toModel(penCreateRequest));
        return CoreApiResponse.success(penResponse);
    }

    @PutMapping("/{id}")
    public CoreApiResponse<PenResponse> updatePen(
            @PathVariable Long id,
            @Valid @RequestBody PenUpdateRequest penUpdateRequest
    ) {
        PenResponse penResponse = penServices.updatePen(id, penUpdateRequest);
        return CoreApiResponse.success(penResponse);
    }

    @GetMapping("/{id}")
    public CoreApiResponse<PenResponse> getPenById(@PathVariable Long id) {
        PenResponse penResponse = penServices.getPenById(id);
        return CoreApiResponse.success(penResponse);
    }

    @GetMapping
    public CoreApiResponse<List<PenResponse>> getAllPens() {
        List<PenResponse> pens = penServices.getAllPens();
        return CoreApiResponse.success(pens);
    }

    @GetMapping("/available")
    public CoreApiResponse<List<PenEntity>> getAvailablePen() {
        // Get available pens
        List<PenEntity> availablePens = penServices.getAvailablePens(LocalDate.now());

        // Return the response
        return CoreApiResponse.success(availablePens);
    }

    @GetMapping("/area/{id}")
    public CoreApiResponse<List<PenEntity>> getPenByArea(@PathVariable Long id) {
        return CoreApiResponse.success(penServices.getPenByArea(id));
    }

    @GetMapping("/status-count/{areaId}")
    public CoreApiResponse<PenStatusCountResponse> getPenStatusCount(@PathVariable Long areaId) {
        PenStatusCountResponse result = penServices.getPenStatusCountByArea(areaId);
        return CoreApiResponse.success(result);
    }

    @GetMapping("/available/cow")
    public CoreApiResponse<List<PenResponse>> getAvailablePens(
            @RequestParam Long cowTypeId,
            @RequestParam CowStatus cowStatus,
            @RequestParam AreaType areaType
    ) {
        List<PenResponse> pens = penServices.getPensByCowTypeAndStatus(cowTypeId, cowStatus, areaType);
        return CoreApiResponse.success(pens);
    }
}
