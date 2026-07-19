package com.company.scopery.modules.profitability.riskflag.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.riskflag.application.action.CloseRiskFlagAction;
import com.company.scopery.modules.profitability.riskflag.application.action.CreateRiskFlagAction;
import com.company.scopery.modules.profitability.riskflag.application.action.MitigateRiskFlagAction;
import com.company.scopery.modules.profitability.riskflag.application.response.ProfitRiskFlagResponse;
import com.company.scopery.modules.profitability.riskflag.application.service.ProfitRiskFlagQueryService;
import com.company.scopery.modules.profitability.riskflag.http.request.CreateRiskFlagRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.RISK_FLAGS)
@Tag(name = "Profitability - Risk Flags")
public class ProfitRiskFlagController {
    private final CreateRiskFlagAction create;
    private final MitigateRiskFlagAction mitigate;
    private final CloseRiskFlagAction close;
    private final ProfitRiskFlagQueryService query;

    public ProfitRiskFlagController(CreateRiskFlagAction create,
                                     MitigateRiskFlagAction mitigate,
                                     CloseRiskFlagAction close,
                                     ProfitRiskFlagQueryService query) {
        this.create = create;
        this.mitigate = mitigate;
        this.close = close;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create risk flag")
    public ApiResponse<ProfitRiskFlagResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateRiskFlagRequest request) {
        return ApiResponse.success(create.execute(projectId, request.reason(), request.impactType(), request.amountAtRisk()));
    }

    @GetMapping
    @Operation(summary = "List risk flags for project")
    public ApiResponse<List<ProfitRiskFlagResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listByProject(projectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get risk flag by id")
    public ApiResponse<ProfitRiskFlagResponse> getById(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(query.getById(projectId, id));
    }

    @PostMapping("/{id}/mitigate")
    @Operation(summary = "Mitigate risk flag")
    public ApiResponse<ProfitRiskFlagResponse> mitigate(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(mitigate.execute(projectId, id));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close risk flag")
    public ApiResponse<ProfitRiskFlagResponse> close(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(close.execute(projectId, id));
    }
}
