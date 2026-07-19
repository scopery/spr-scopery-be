package com.company.scopery.modules.profitability.costforecast.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.costforecast.application.action.ArchiveCostForecastAction;
import com.company.scopery.modules.profitability.costforecast.application.action.CreateCostForecastAction;
import com.company.scopery.modules.profitability.costforecast.application.response.ProfitCostForecastResponse;
import com.company.scopery.modules.profitability.costforecast.application.service.ProfitCostForecastQueryService;
import com.company.scopery.modules.profitability.costforecast.http.request.CreateCostForecastRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.COST_FORECASTS)
@Tag(name = "Profitability - Cost Forecasts")
public class ProfitCostForecastController {
    private final CreateCostForecastAction create;
    private final ArchiveCostForecastAction archive;
    private final ProfitCostForecastQueryService query;

    public ProfitCostForecastController(CreateCostForecastAction create,
                                        ArchiveCostForecastAction archive,
                                        ProfitCostForecastQueryService query) {
        this.create = create;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create cost forecast")
    public ApiResponse<ProfitCostForecastResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateCostForecastRequest request) {
        return ApiResponse.success(create.execute(
                projectId, request.forecastType(), request.currency(),
                request.forecastAmount(), request.confidencePercent(),
                request.forecastDate(), request.assumptionNotes()));
    }

    @PostMapping("/{forecastId}/archive")
    @Operation(summary = "Archive cost forecast")
    public ApiResponse<ProfitCostForecastResponse> archive(
            @PathVariable UUID projectId,
            @PathVariable UUID forecastId) {
        return ApiResponse.success(archive.execute(projectId, forecastId));
    }

    @GetMapping
    @Operation(summary = "List cost forecasts")
    public ApiResponse<List<ProfitCostForecastResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{forecastId}")
    @Operation(summary = "Get cost forecast")
    public ApiResponse<ProfitCostForecastResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID forecastId) {
        return ApiResponse.success(query.get(projectId, forecastId));
    }
}
