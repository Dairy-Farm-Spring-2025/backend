package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.requests.PenCreateRequest;
import com.capstone.dfms.requests.PenUpdateRequest;
import com.capstone.dfms.responses.PenResponse;
import com.capstone.dfms.services.IPenServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        PenResponse penResponse = penServices.updatePen(id, INSTANCE.toModel(penUpdateRequest));
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
}
