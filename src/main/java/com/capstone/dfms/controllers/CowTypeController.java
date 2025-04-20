package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.requests.CowTypeCreateRequest;
import com.capstone.dfms.requests.CowTypeUpdateRequest;
import com.capstone.dfms.responses.CowTypeResponse;
import com.capstone.dfms.services.ICowTypeServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.ICowTypeMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/cow-types")
@RequiredArgsConstructor
public class CowTypeController {
    private final ICowTypeServices cowTypeServices;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<CowTypeResponse> createCowType(@Valid @RequestBody CowTypeCreateRequest cowTypeCreateRequest) {
        var cowTypeResponse = cowTypeServices.createCowType(INSTANCE.toModel(cowTypeCreateRequest));
        return CoreApiResponse.success(cowTypeResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<CowTypeResponse> updateCowType(
            @PathVariable Long id,
            @Valid @RequestBody CowTypeUpdateRequest cowTypeUpdateRequest) {
        CowTypeResponse cowTypeResponse = cowTypeServices.updateCowType(id, INSTANCE.toModel(cowTypeUpdateRequest));
        return CoreApiResponse.success(cowTypeResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping("/{id}")
    public CoreApiResponse<CowTypeResponse> getCowTypeById(@PathVariable Long id) {
        CowTypeResponse cowTypeResponse = cowTypeServices.getCowTypeById(id);
        return CoreApiResponse.success(cowTypeResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','VETERINARIANS','WORKER')")
    @GetMapping
    public CoreApiResponse<List<CowTypeResponse>> getAllCowTypes() {
        List<CowTypeResponse> cowTypes = cowTypeServices.getAllCowTypes();
        return CoreApiResponse.success(cowTypes);
    }
}
