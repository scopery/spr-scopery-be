package com.company.scopery.modules.profitability.revenueforecast.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.revenueforecast.application.action.ArchiveRevenueForecastAction;
import com.company.scopery.modules.profitability.revenueforecast.application.action.CreateRevenueForecastAction;
import com.company.scopery.modules.profitability.revenueforecast.application.response.ProfitRevenueForecastResponse;
import com.company.scopery.modules.profitability.revenueforecast.application.service.ProfitRevenueForecastQueryService;
import com.company.scopery.modules.profitability.revenueforecast.http.request.CreateRevenueForecastRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.REVENUE_FORECASTS)
@Tag(name = "Profitability - Revenue Forecasts")
public class ProfitRevenueForecastController {
    private final CreateRevenueForecastAction create;
    private final ArchiveRevenueForecastAction archive;
    private final ProfitRevenueForecastQueryService query;

    public ProfitRevenueForecastController(CreateRevenueForecastAction create,
                                            ArchiveRevenueForecastAction archive,
                                            ProfitRevenueForecastQueryService query) {
        this.create = create;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create revenue forecast")
    public ApiResponse<ProfitRevenueForecastResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateRevenueForecastRequest request) {
        return ApiResponse.success(create.execute(
                projectId, request.forecastType(), request.currency(),
                request.forecastAmount(), request.confidencePercent(),
                request.forecastDate(), request.assumptionNotes()));
    }

    @PostMapping("/{forecastId}/archive")
    @Operation(summary = "Archive revenue forecast")
    public ApiResponse<ProfitRevenueForecastResponse> archive(
            @PathVariable UUID projectId,
            @PathVariable UUID forecastId) {
        return ApiResponse.success(archive.execute(projectId, forecastId));
    }

    @GetMapping
    @Operation(summary = "List revenue forecasts")
    public ApiResponse<List<ProfitRevenueForecastResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{forecastId}")
    @Operation(summary = "Get revenue forecast")
    public ApiResponse<ProfitRevenueForecastResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID forecastId) {
        return ApiResponse.success(query.get(projectId, forecastId));
    }
}
