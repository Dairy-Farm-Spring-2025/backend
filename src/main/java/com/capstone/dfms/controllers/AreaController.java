package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.requests.AreaCreateRequest;
import com.capstone.dfms.requests.AreaUpdateRequest;
import com.capstone.dfms.responses.AreaResponse;
import com.capstone.dfms.services.IAreaServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.capstone.dfms.mappers.IAreaMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/areas")
@RequiredArgsConstructor
public class AreaController {
    private final IAreaServices areaServices;

    @PostMapping("/create")
    public CoreApiResponse<AreaResponse> createAccountManager(
            @Valid @RequestBody AreaCreateRequest areaCreateRequest
    ){
        var areaResponse = areaServices.createArea(INSTANCE.toModel(areaCreateRequest));
        return CoreApiResponse.success(areaResponse);
    }

    @PutMapping("/{id}")
    public CoreApiResponse<AreaResponse> updateArea(
            @PathVariable Long id,
            @Valid @RequestBody AreaUpdateRequest areaUpdateRequest
    ) {
        AreaResponse areaResponse = areaServices.updateArea(id, INSTANCE.toModel(areaUpdateRequest));
        return CoreApiResponse.success(areaResponse);
    }

    @GetMapping("/{id}")
    public CoreApiResponse<AreaResponse> getAreaById(@PathVariable Long id) {
        AreaResponse areaResponse = areaServices.getAreaById(id);
        return CoreApiResponse.success(areaResponse);
    }

    @GetMapping
    public CoreApiResponse<List<AreaResponse>> getAllAreas() {
        List<AreaResponse> areas = areaServices.getAllAreas();
        return CoreApiResponse.success(areas);
    }
}
