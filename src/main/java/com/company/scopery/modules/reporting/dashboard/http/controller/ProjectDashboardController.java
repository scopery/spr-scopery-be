package com.company.scopery.modules.reporting.dashboard.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectAttentionResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectDashboardResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectHealthResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ProjectKpisResponse;
import com.company.scopery.modules.reporting.dashboard.application.service.ProjectDashboardQueryService;
import com.company.scopery.modules.reporting.shared.constant.ReportingApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ReportingApiPaths.DASHBOARD)
@Tag(name = "Reporting - Dashboard")
public class ProjectDashboardController {
    private final ProjectDashboardQueryService query;

    public ProjectDashboardController(ProjectDashboardQueryService query) {
        this.query = query;
    }

    @GetMapping
    @Operation(summary = "Project dashboard summary")
    public ApiResponse<ProjectDashboardResponse> dashboard(@PathVariable UUID projectId) {
        return ApiResponse.success(query.dashboard(projectId));
    }

    @GetMapping("/health")
    @Operation(summary = "Project health score")
    public ApiResponse<ProjectHealthResponse> health(@PathVariable UUID projectId) {
        return ApiResponse.success(query.health(projectId));
    }

    @GetMapping("/kpis")
    @Operation(summary = "Project KPIs")
    public ApiResponse<ProjectKpisResponse> kpis(@PathVariable UUID projectId) {
        return ApiResponse.success(query.kpis(projectId));
    }

    @GetMapping("/attention")
    @Operation(summary = "Project attention items")
    public ApiResponse<ProjectAttentionResponse> attention(@PathVariable UUID projectId) {
        return ApiResponse.success(query.attention(projectId));
    }
}
