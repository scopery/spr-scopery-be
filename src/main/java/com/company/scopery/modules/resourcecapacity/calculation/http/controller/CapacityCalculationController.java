package com.company.scopery.modules.resourcecapacity.calculation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.CalculateCapacityQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.OverAllocationQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.ProjectAllocationSummaryQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.UserAvailabilityQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.query.WorkspaceOverviewQuery;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.CapacityCalculationResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.OverAllocationResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.ProjectAllocationSummaryResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.UserAvailabilityResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.response.WorkspaceOverviewResponse;
import com.company.scopery.modules.resourcecapacity.calculation.application.service.CapacityCalculationService;
import com.company.scopery.modules.resourcecapacity.calculation.http.request.CalculateCapacityRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@Tag(name = "Resource Capacity - Calculation")
public class CapacityCalculationController {

    private final CapacityCalculationService calculationService;

    public CapacityCalculationController(CapacityCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @GetMapping(CapacityApiPaths.USER_AVAILABILITY)
    @Operation(summary = "Get a user's capacity availability over a date range")
    public ApiResponse<UserAvailabilityResponse> getUserAvailability(
            @RequestParam UUID workspaceId,
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        var query = new UserAvailabilityQuery(workspaceId, userId, fromDate, toDate);
        return ApiResponse.success(calculationService.getUserAvailability(query));
    }

    @GetMapping(CapacityApiPaths.WORKSPACE_OVERVIEW)
    @Operation(summary = "Get a workspace-wide capacity overview over a date range")
    public ApiResponse<WorkspaceOverviewResponse> getWorkspaceOverview(
            @PathVariable UUID workspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        var query = new WorkspaceOverviewQuery(workspaceId, fromDate, toDate);
        return ApiResponse.success(calculationService.getWorkspaceOverview(query));
    }

    @GetMapping(CapacityApiPaths.PROJECT_ALLOCATION_SUMMARY)
    @Operation(summary = "Get a project's resource allocation summary over a date range")
    public ApiResponse<ProjectAllocationSummaryResponse> getProjectAllocationSummary(
            @PathVariable UUID projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        var query = new ProjectAllocationSummaryQuery(projectId, fromDate, toDate);
        return ApiResponse.success(calculationService.getProjectAllocationSummary(query));
    }

    @GetMapping(CapacityApiPaths.OVER_ALLOCATIONS)
    @Operation(summary = "List workspace members whose overlapping allocations exceed 100%")
    public ApiResponse<OverAllocationResponse> getOverAllocations(
            @RequestParam UUID workspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        var query = new OverAllocationQuery(workspaceId, fromDate, toDate);
        return ApiResponse.success(calculationService.getOverAllocations(query));
    }

    @PostMapping(CapacityApiPaths.CALCULATE)
    @Operation(summary = "Calculate a user's working/focused/project-allocated hours over a date range")
    public ApiResponse<CapacityCalculationResponse> calculate(
            @RequestParam UUID workspaceId,
            @Valid @RequestBody CalculateCapacityRequest request) {
        var query = new CalculateCapacityQuery(
                workspaceId, request.userId(), request.projectId(), request.fromDate(), request.toDate());
        return ApiResponse.success(calculationService.calculate(query));
    }
}
