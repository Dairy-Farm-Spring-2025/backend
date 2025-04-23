package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.requests.AreaCreateRequest;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.services.IAreaServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IAreaMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/areas")
@RequiredArgsConstructor
public class AreaController {
    private final IAreaServices areaServices;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/create")
    public CoreApiResponse<AreaResponse> createArea(@Valid @RequestBody AreaCreateRequest areaCreateRequest) {
        var areaResponse = areaServices.createArea(INSTANCE.toModel(areaCreateRequest));
        String message = LocalizationUtils.getMessage("general.create_successfully");
        return CoreApiResponse.success(areaResponse, message);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public CoreApiResponse<AreaResponse> updateArea(
            @PathVariable Long id,
            @Valid @RequestBody AreaUpdateRequest areaUpdateRequest
    ) {
        AreaResponse areaResponse = areaServices.updateArea(id, areaUpdateRequest);
        return CoreApiResponse.success(areaResponse,LocalizationUtils.getMessage("general.update_successfully") );
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping("/{id}")
    public CoreApiResponse<AreaResponse> getAreaById(@PathVariable Long id) {
        AreaResponse areaResponse = areaServices.getAreaById(id);
        return CoreApiResponse.success(areaResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','WORKER','VETERINARIANS','MANAGER')")
    @GetMapping
    public CoreApiResponse<List<AreaResponse>> getAllAreas() {
        List<AreaResponse> areas = areaServices.getAllAreas();
        return CoreApiResponse.success(areas);
    }
}
