package com.company.scopery.modules.resourcecapacity.utilization.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.planning.application.response.UtilizationSummaryResponse;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import com.company.scopery.modules.resourcecapacity.utilization.application.action.RebuildUtilizationSummaryAction;
import com.company.scopery.modules.resourcecapacity.utilization.http.request.RebuildUtilizationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Resource Capacity - Utilization")
public class ResourceUtilizationController {
    private final RebuildUtilizationSummaryAction rebuild;

    public ResourceUtilizationController(RebuildUtilizationSummaryAction rebuild) {
        this.rebuild = rebuild;
    }

    @PostMapping(CapacityApiPaths.UTILIZATION_REBUILD)
    @Operation(summary = "Rebuild utilization summary for a resource profile")
    public ApiResponse<UtilizationSummaryResponse> rebuild(
            @PathVariable UUID workspaceId,
            @PathVariable UUID resourceId,
            @RequestBody(required = false) RebuildUtilizationRequest request) {
        var effortHours = request != null ? request.effortHours() : null;
        var availableHours = request != null ? request.availableCapacityHours() : null;
        return ApiResponse.success(rebuild.execute(workspaceId, resourceId, effortHours, availableHours));
    }
}
