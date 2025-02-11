package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.requests.CowCreateRequest;
import com.capstone.dfms.requests.CowUpdateRequest;
import com.capstone.dfms.responses.CowResponse;
import com.capstone.dfms.services.ICowServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.dfms.mappers.ICowMapper.INSTANCE;

@RestController
@RequestMapping("${app.api.version.v1}/cows")
@RequiredArgsConstructor
public class CowController {
    private final ICowServices cowServices;

    @PostMapping("/create")
    public CoreApiResponse<CowResponse> createCow(@Valid @RequestBody CowCreateRequest cowCreateRequest) {
        CowResponse cowResponse = cowServices.createCow(INSTANCE.toModel(cowCreateRequest));
        return CoreApiResponse.success(cowResponse);
    }

    @PutMapping("/{id}")
    public CoreApiResponse<CowResponse> updateCow(@PathVariable Long id, @Valid @RequestBody CowUpdateRequest cowUpdateRequest) {
        CowResponse cowResponse = cowServices.updateCow(id, cowUpdateRequest);
        return CoreApiResponse.success(cowResponse);
    }

    @GetMapping("/{id}")
    public CoreApiResponse<CowResponse> getCowById(@PathVariable Long id) {
        CowResponse cowResponse = cowServices.getCowById(id);
        return CoreApiResponse.success(cowResponse);
    }

    @GetMapping
    public CoreApiResponse<List<CowResponse>> getAllCows() {
        List<CowResponse> cowResponses = cowServices.getAllCows();
        return CoreApiResponse.success(cowResponses);
    }
}
