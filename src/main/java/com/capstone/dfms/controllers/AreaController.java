package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.components.utils.LocalizationUtils;
import com.capstone.dfms.requests.AreaCreateRequest;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.services.IAreaServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.IAreaMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/areas")
@RequiredArgsConstructor
public class AreaController {
    private final IAreaServices areaServices;

    @PostMapping("/create")
    public CoreApiResponse<AreaResponse> createArea(@Valid @RequestBody AreaCreateRequest areaCreateRequest) {
        var areaResponse = areaServices.createArea(INSTANCE.toModel(areaCreateRequest));
        String message = LocalizationUtils.getMessage("general.create_successfully");
        return CoreApiResponse.success(areaResponse, message);
    }

    @PutMapping("/{id}")
    public CoreApiResponse<AreaResponse> updateArea(
            @PathVariable Long id,
            @Valid @RequestBody AreaUpdateRequest areaUpdateRequest
    ) {
        AreaResponse areaResponse = areaServices.updateArea(id, areaUpdateRequest);
        String message = LocalizationUtils.getMessage("area.update.success");
        return CoreApiResponse.success(areaResponse, message);
    }

    @GetMapping("/{id}")
    public CoreApiResponse<AreaResponse> getAreaById(@PathVariable Long id) {
        AreaResponse areaResponse = areaServices.getAreaById(id);
        String message = LocalizationUtils.getMessage("area.get.success");
        return CoreApiResponse.success(areaResponse, message);
    }

    @GetMapping
    public CoreApiResponse<List<AreaResponse>> getAllAreas() {
        List<AreaResponse> areas = areaServices.getAllAreas();
        String message = LocalizationUtils.getMessage("area.get.success");
        return CoreApiResponse.success(areas, message);
    }
}
