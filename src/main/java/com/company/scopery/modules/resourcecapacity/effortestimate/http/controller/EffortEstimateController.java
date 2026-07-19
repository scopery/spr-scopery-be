package com.company.scopery.modules.resourcecapacity.effortestimate.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcecapacity.effortestimate.application.action.CreateEffortEstimateAction;
import com.company.scopery.modules.resourcecapacity.effortestimate.application.response.EffortEstimateResponse;
import com.company.scopery.modules.resourcecapacity.effortestimate.application.service.EffortEstimateQueryService;
import com.company.scopery.modules.resourcecapacity.effortestimate.http.request.CreateEffortEstimateRequest;
import com.company.scopery.modules.resourcecapacity.shared.constant.CapacityApiPaths;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController @Tag(name = "Resource Capacity - Effort")
public class EffortEstimateController {
    private final CreateEffortEstimateAction create; private final EffortEstimateQueryService query;
    public EffortEstimateController(CreateEffortEstimateAction create, EffortEstimateQueryService query) { this.create=create; this.query=query; }
    @PostMapping(CapacityApiPaths.EFFORT_ESTIMATES) @Operation(summary="Create effort estimate")
    public ApiResponse<EffortEstimateResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateEffortEstimateRequest r) {
        return ApiResponse.success(create.execute(projectId, r));
    }
    @GetMapping(CapacityApiPaths.EFFORT_ESTIMATES) @Operation(summary="List effort estimates")
    public ApiResponse<List<EffortEstimateResponse>> list(@PathVariable UUID projectId) { return ApiResponse.success(query.list(projectId)); }
}
