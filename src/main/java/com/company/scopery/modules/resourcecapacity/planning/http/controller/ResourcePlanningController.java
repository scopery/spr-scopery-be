package com.company.scopery.modules.resourcecapacity.planning.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.action.CloseResourceRiskFlagAction;
import com.company.scopery.modules.resourcecapacity.planning.application.action.CreateResourceRiskFlagAction;
import com.company.scopery.modules.resourcecapacity.planning.application.action.MitigateResourceRiskFlagAction;
import com.company.scopery.modules.resourcecapacity.planning.application.action.RebuildCapacitySummaryAction;
import com.company.scopery.modules.resourcecapacity.planning.application.action.RebuildCostInputAction;
import com.company.scopery.modules.resourcecapacity.planning.application.action.RebuildEffortForecastAction;
import com.company.scopery.modules.resourcecapacity.planning.application.response.EffortForecastResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ProjectCapacitySummaryResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ResourceCostInputResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ResourceRiskFlagResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.service.ResourceRiskFlagQueryService;
import com.company.scopery.modules.resourcecapacity.planning.http.request.CreateRiskFlagRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Resource Capacity - Planning")
public class ResourcePlanningController {
    private final RebuildEffortForecastAction forecast;
    private final RebuildCapacitySummaryAction capacity;
    private final RebuildCostInputAction cost;
    private final CreateResourceRiskFlagAction createRisk;
    private final MitigateResourceRiskFlagAction mitigateRisk;
    private final CloseResourceRiskFlagAction closeRisk;
    private final ResourceRiskFlagQueryService riskQuery;

    public ResourcePlanningController(
            RebuildEffortForecastAction forecast,
            RebuildCapacitySummaryAction capacity,
            RebuildCostInputAction cost,
            CreateResourceRiskFlagAction createRisk,
            MitigateResourceRiskFlagAction mitigateRisk,
            CloseResourceRiskFlagAction closeRisk,
            ResourceRiskFlagQueryService riskQuery) {
        this.forecast = forecast;
        this.capacity = capacity;
        this.cost = cost;
        this.createRisk = createRisk;
        this.mitigateRisk = mitigateRisk;
        this.closeRisk = closeRisk;
        this.riskQuery = riskQuery;
    }

    @PostMapping(CapacityApiPaths.EFFORT_FORECAST_REBUILD)
    @Operation(summary = "Rebuild effort forecast")
    public ApiResponse<EffortForecastResponse> rebuildForecast(@PathVariable UUID projectId) {
        return ApiResponse.success(forecast.execute(projectId));
    }

    @PostMapping(CapacityApiPaths.CAPACITY_SUMMARY_REBUILD)
    @Operation(summary = "Rebuild project capacity summary")
    public ApiResponse<ProjectCapacitySummaryResponse> rebuildCapacity(@PathVariable UUID projectId) {
        return ApiResponse.success(capacity.execute(projectId));
    }

    @PostMapping(CapacityApiPaths.COST_INPUTS_REBUILD)
    @Operation(summary = "Rebuild resource cost inputs")
    public ApiResponse<ResourceCostInputResponse> rebuildCost(
            @PathVariable UUID projectId, @RequestParam(defaultValue = "false") boolean includeSensitive) {
        return ApiResponse.success(cost.execute(projectId, includeSensitive));
    }

    @GetMapping(CapacityApiPaths.COST_INPUTS)
    @Operation(summary = "Get resource cost inputs (masked by default)")
    public ApiResponse<ResourceCostInputResponse> getCost(
            @PathVariable UUID projectId, @RequestParam(defaultValue = "false") boolean includeSensitive) {
        return ApiResponse.success(cost.execute(projectId, includeSensitive));
    }

    @PostMapping(CapacityApiPaths.RISK_FLAGS)
    @Operation(summary = "Create resource risk flag")
    public ApiResponse<ResourceRiskFlagResponse> createRisk(
            @PathVariable UUID projectId, @Valid @RequestBody CreateRiskFlagRequest r) {
        return ApiResponse.success(createRisk.execute(
                projectId, r.riskReason(), r.impactType(), r.description(), r.resourceProfileId()));
    }

    @GetMapping(CapacityApiPaths.RISK_FLAGS)
    @Operation(summary = "List resource risk flags")
    public ApiResponse<List<ResourceRiskFlagResponse>> listRisks(@PathVariable UUID projectId) {
        return ApiResponse.success(riskQuery.listByProject(projectId));
    }

    @PostMapping(CapacityApiPaths.RISK_FLAGS + "/{riskFlagId}/mitigate")
    @Operation(summary = "Mitigate resource risk flag")
    public ApiResponse<ResourceRiskFlagResponse> mitigate(
            @PathVariable UUID projectId, @PathVariable UUID riskFlagId) {
        return ApiResponse.success(mitigateRisk.execute(projectId, riskFlagId));
    }

    @PostMapping(CapacityApiPaths.RISK_FLAGS + "/{riskFlagId}/close")
    @Operation(summary = "Close resource risk flag")
    public ApiResponse<ResourceRiskFlagResponse> close(
            @PathVariable UUID projectId, @PathVariable UUID riskFlagId) {
        return ApiResponse.success(closeRisk.execute(projectId, riskFlagId));
    }
}
