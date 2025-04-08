package com.capstone.dfms.controllers;

import com.capstone.dfms.components.apis.CoreApiResponse;
import com.capstone.dfms.responses.DashboardResponse;
import com.capstone.dfms.services.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.version.v1}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/today")
    public CoreApiResponse<DashboardResponse> getTodayStats() {
        DashboardResponse response = dashboardService.getTodayStats();
        return CoreApiResponse.success(response);
    }
}
