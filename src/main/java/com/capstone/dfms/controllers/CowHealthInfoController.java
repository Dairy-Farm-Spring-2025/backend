package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.responses.CowHealthInfoResponse;
import com.capstone.dfms.services.ICowHealthInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.version.v1}/cow-health-info")
@RequiredArgsConstructor
public class CowHealthInfoController {
    private final ICowHealthInfoService cowHealthInfoService;

    @PreAuthorize("hasAnyRole('WORKER','MANAGER','VETERINARIANS')")
    @GetMapping("/{cowId}")
    public CoreApiResponse<List<CowHealthInfoResponse<?>>> getAllCowHealthInfo(@PathVariable Long cowId) {
        List<CowHealthInfoResponse<?>> responses = cowHealthInfoService.getAllHealthInfoOrderedDesc(cowId);
        return CoreApiResponse.success(responses);
    }

}
