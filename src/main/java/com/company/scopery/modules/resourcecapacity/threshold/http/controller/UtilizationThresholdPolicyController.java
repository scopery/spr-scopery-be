package com.company.scopery.modules.resourcecapacity.threshold.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.response.ThresholdPolicyResponse;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import com.company.scopery.modules.resourcecapacity.threshold.application.action.UpsertThresholdPolicyAction;
import com.company.scopery.modules.resourcecapacity.threshold.application.service.ThresholdPolicyQueryService;
import com.company.scopery.modules.resourcecapacity.threshold.http.request.UpsertThresholdPolicyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Resource Capacity - Threshold Policy")
public class UtilizationThresholdPolicyController {
    private final UpsertThresholdPolicyAction upsert;
    private final ThresholdPolicyQueryService query;

    public UtilizationThresholdPolicyController(UpsertThresholdPolicyAction upsert,
                                                ThresholdPolicyQueryService query) {
        this.upsert = upsert;
        this.query = query;
    }

    @GetMapping(CapacityApiPaths.THRESHOLD_WS)
    @Operation(summary = "Get workspace utilization threshold policy")
    public ApiResponse<ThresholdPolicyResponse> getWs(@PathVariable UUID workspaceId) {
        return ApiResponse.success(query.getForWorkspace(workspaceId));
    }

    @PutMapping(CapacityApiPaths.THRESHOLD_WS)
    @Operation(summary = "Upsert workspace utilization threshold policy")
    public ApiResponse<ThresholdPolicyResponse> putWs(
            @PathVariable UUID workspaceId,
            @RequestBody UpsertThresholdPolicyRequest request) {
        return ApiResponse.success(upsert.executeForWorkspace(workspaceId,
                request.underAllocatedPercent(), request.healthyMinPercent(), request.healthyMaxPercent(),
                request.watchMaxPercent(), request.overloadedPercent(), request.criticalOverloadPercent()));
    }

    @GetMapping(CapacityApiPaths.THRESHOLD_PR)
    @Operation(summary = "Get project utilization threshold policy")
    public ApiResponse<ThresholdPolicyResponse> getPr(@PathVariable UUID projectId) {
        return ApiResponse.success(query.getForProject(projectId));
    }

    @PutMapping(CapacityApiPaths.THRESHOLD_PR)
    @Operation(summary = "Upsert project utilization threshold policy")
    public ApiResponse<ThresholdPolicyResponse> putPr(
            @PathVariable UUID projectId,
            @RequestBody UpsertThresholdPolicyRequest request) {
        return ApiResponse.success(upsert.executeForProject(projectId,
                request.underAllocatedPercent(), request.healthyMinPercent(), request.healthyMaxPercent(),
                request.watchMaxPercent(), request.overloadedPercent(), request.criticalOverloadPercent()));
    }
}
