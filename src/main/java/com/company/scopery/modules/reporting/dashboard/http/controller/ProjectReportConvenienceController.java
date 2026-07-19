package com.company.scopery.modules.reporting.dashboard.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.AiPlanningReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.BaselineVsCurrentReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.CapacityReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ChangeImpactReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.EstimationReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.FinanceReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.NotificationsReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.QuoteReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.ScheduleRiskReportResponse;
import com.company.scopery.modules.reporting.dashboard.application.response.TaskRiskReportResponse;
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
@RequestMapping(ReportingApiPaths.PROJECT_REPORTS)
@Tag(name = "Reporting - Project Reports")
public class ProjectReportConvenienceController {
    private final ProjectDashboardQueryService dashboard;

    public ProjectReportConvenienceController(ProjectDashboardQueryService dashboard) {
        this.dashboard = dashboard;
    }

    @GetMapping("/task-risk")
    @Operation(summary = "Task risk report")
    public ApiResponse<TaskRiskReportResponse> taskRisk(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.taskRisk(projectId));
    }

    @GetMapping("/schedule-risk")
    @Operation(summary = "Schedule risk report")
    public ApiResponse<ScheduleRiskReportResponse> scheduleRisk(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.scheduleRisk(projectId));
    }

    @GetMapping("/capacity")
    @Operation(summary = "Capacity report")
    public ApiResponse<CapacityReportResponse> capacity(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.capacity(projectId));
    }

    @GetMapping("/estimation")
    @Operation(summary = "Estimation report")
    public ApiResponse<EstimationReportResponse> estimation(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.estimation(projectId));
    }

    @GetMapping("/finance")
    @Operation(summary = "Finance report")
    public ApiResponse<FinanceReportResponse> finance(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.finance(projectId));
    }

    @GetMapping("/quote")
    @Operation(summary = "Quote report")
    public ApiResponse<QuoteReportResponse> quote(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.quote(projectId));
    }

    @GetMapping("/baseline-vs-current")
    @Operation(summary = "Baseline vs current report")
    public ApiResponse<BaselineVsCurrentReportResponse> baselineVsCurrent(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.baselineVsCurrent(projectId));
    }

    @GetMapping("/change-impact")
    @Operation(summary = "Change impact report")
    public ApiResponse<ChangeImpactReportResponse> changeImpact(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.changeImpact(projectId));
    }

    @GetMapping("/notifications")
    @Operation(summary = "Notification attention report")
    public ApiResponse<NotificationsReportResponse> notifications(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.notifications(projectId));
    }

    @GetMapping("/ai-planning")
    @Operation(summary = "AI planning report")
    public ApiResponse<AiPlanningReportResponse> aiPlanning(@PathVariable UUID projectId) {
        return ApiResponse.success(dashboard.aiPlanning(projectId));
    }
}
