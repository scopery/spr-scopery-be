package com.company.scopery.modules.servicesupport.snapshot.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.servicesupport.shared.constant.SupportApiPaths;
import com.company.scopery.modules.servicesupport.snapshot.application.response.SupportDashboardResponse;
import com.company.scopery.modules.servicesupport.snapshot.application.service.SupportDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Service Support - Dashboard")
public class SupportDashboardController {
    private final SupportDashboardService dashboardService;

    public SupportDashboardController(SupportDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping(SupportApiPaths.DASHBOARD)
    @Operation(summary = "Get support dashboard summary")
    public ApiResponse<SupportDashboardResponse> dashboard(@PathVariable UUID workspaceId) {
        return ApiResponse.success(dashboardService.dashboard(workspaceId));
    }
}
