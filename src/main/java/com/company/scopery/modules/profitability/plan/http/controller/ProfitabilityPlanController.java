package com.company.scopery.modules.profitability.plan.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.plan.application.action.CreateProfitabilityPlanAction;
import com.company.scopery.modules.profitability.plan.application.action.FinalizePlanVersionAction;
import com.company.scopery.modules.profitability.plan.application.response.ProfitabilityPlanResponse;
import com.company.scopery.modules.profitability.plan.application.response.ProfitabilityPlanVersionResponse;
import com.company.scopery.modules.profitability.plan.application.service.ProfitabilityPlanQueryService;
import com.company.scopery.modules.profitability.plan.http.request.CreatePlanRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.PLANS)
@Tag(name = "Profitability - Plans")
public class ProfitabilityPlanController {
    private final CreateProfitabilityPlanAction create;
    private final FinalizePlanVersionAction finalize;
    private final ProfitabilityPlanQueryService query;

    public ProfitabilityPlanController(CreateProfitabilityPlanAction create,
                                       FinalizePlanVersionAction finalize,
                                       ProfitabilityPlanQueryService query) {
        this.create = create;
        this.finalize = finalize;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create profitability plan")
    public ApiResponse<ProfitabilityPlanResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreatePlanRequest request) {
        return ApiResponse.success(create.execute(
                projectId, request.planCode(), request.name(), request.planType(),
                request.versionLabel(), request.currency(),
                request.plannedRevenue(), request.plannedCost(), request.plannedProfit(),
                request.plannedMarginPercent(), request.assumptionNotes()));
    }

    @GetMapping
    @Operation(summary = "List profitability plans")
    public ApiResponse<List<ProfitabilityPlanResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listByProject(projectId));
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get profitability plan")
    public ApiResponse<ProfitabilityPlanResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID planId) {
        return ApiResponse.success(query.getPlan(projectId, planId));
    }

    @GetMapping("/{planId}/versions")
    @Operation(summary = "List plan versions")
    public ApiResponse<List<ProfitabilityPlanVersionResponse>> listVersions(
            @PathVariable UUID projectId,
            @PathVariable UUID planId) {
        return ApiResponse.success(query.listVersions(projectId, planId));
    }

    @GetMapping("/{planId}/versions/{versionId}")
    @Operation(summary = "Get plan version")
    public ApiResponse<ProfitabilityPlanVersionResponse> getVersion(
            @PathVariable UUID projectId,
            @PathVariable UUID planId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(query.getVersion(projectId, planId, versionId));
    }

    @PostMapping("/{planId}/versions/{versionId}/finalize")
    @Operation(summary = "Finalize plan version")
    public ApiResponse<ProfitabilityPlanVersionResponse> finalize(
            @PathVariable UUID projectId,
            @PathVariable UUID planId,
            @PathVariable UUID versionId) {
        return ApiResponse.success(finalize.execute(projectId, planId, versionId));
    }
}
