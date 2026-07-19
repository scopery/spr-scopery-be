package com.company.scopery.modules.profitability.variance.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import com.company.scopery.modules.profitability.variance.application.action.CalculateVarianceAction;
import com.company.scopery.modules.profitability.variance.application.response.ProfitVarianceResponse;
import com.company.scopery.modules.profitability.variance.application.service.ProfitVarianceQueryService;
import com.company.scopery.modules.profitability.variance.http.request.CreateVarianceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.VARIANCE)
@Tag(name = "Profitability - Variance")
public class ProfitVarianceController {
    private final CalculateVarianceAction calculate;
    private final ProfitVarianceQueryService query;

    public ProfitVarianceController(CalculateVarianceAction calculate,
                                    ProfitVarianceQueryService query) {
        this.calculate = calculate;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Calculate and record variance")
    public ApiResponse<ProfitVarianceResponse> calculate(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateVarianceRequest request) {
        return ApiResponse.success(calculate.execute(
                projectId, request.varianceType(),
                request.fromAmount(), request.toAmount(), request.varianceAmount(),
                request.variancePercent(), request.currency(),
                request.explanation(), request.sourceSnapshotId()));
    }

    @GetMapping
    @Operation(summary = "List variances")
    public ApiResponse<List<ProfitVarianceResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{varianceId}")
    @Operation(summary = "Get variance")
    public ApiResponse<ProfitVarianceResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID varianceId) {
        return ApiResponse.success(query.get(projectId, varianceId));
    }
}
