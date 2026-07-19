package com.company.scopery.modules.integrationhub.dashboard.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.integrationhub.dashboard.application.response.IntegrationDashboardResponse;
import com.company.scopery.modules.integrationhub.dashboard.application.service.IntegrationDashboardService;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @Tag(name = "Integration Hub - Dashboard")
public class IntegrationDashboardController {
    private final IntegrationDashboardService dashboard;
    public IntegrationDashboardController(IntegrationDashboardService dashboard) { this.dashboard = dashboard; }
    @GetMapping(IntegrationApiPaths.DASHBOARD) @Operation(summary = "Integration observability dashboard")
    public ApiResponse<IntegrationDashboardResponse> dashboard(@PathVariable UUID workspaceId) {
        return ApiResponse.success(dashboard.getDashboard(workspaceId));
    }
}
