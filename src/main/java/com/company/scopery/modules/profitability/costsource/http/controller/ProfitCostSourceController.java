package com.company.scopery.modules.profitability.costsource.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.costsource.application.action.ArchiveProfitCostSourceAction;
import com.company.scopery.modules.profitability.costsource.application.action.CreateProfitCostSourceAction;
import com.company.scopery.modules.profitability.costsource.application.action.UpdateProfitCostSourceAction;
import com.company.scopery.modules.profitability.costsource.application.response.ProfitCostSourceResponse;
import com.company.scopery.modules.profitability.costsource.application.service.ProfitCostSourceQueryService;
import com.company.scopery.modules.profitability.costsource.http.request.CreateCostSourceRequest;
import com.company.scopery.modules.profitability.costsource.http.request.UpdateCostSourceRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.COST_SOURCES)
@Tag(name = "Profitability - Cost Sources")
public class ProfitCostSourceController {
    private final CreateProfitCostSourceAction create;
    private final UpdateProfitCostSourceAction update;
    private final ArchiveProfitCostSourceAction archive;
    private final ProfitCostSourceQueryService query;

    public ProfitCostSourceController(CreateProfitCostSourceAction create,
                                      UpdateProfitCostSourceAction update,
                                      ArchiveProfitCostSourceAction archive,
                                      ProfitCostSourceQueryService query) {
        this.create = create;
        this.update = update;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create cost source")
    public ApiResponse<ProfitCostSourceResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateCostSourceRequest request) {
        return ApiResponse.success(create.execute(
                projectId,
                request.sourceType(),
                request.sourceId(),
                request.effortHours(),
                request.rateAmount(),
                request.amount(),
                request.currency(),
                request.includedInForecast() == null || request.includedInForecast()));
    }

    @PutMapping("/{sourceId}")
    @Operation(summary = "Update cost source")
    public ApiResponse<ProfitCostSourceResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID sourceId,
            @Valid @RequestBody UpdateCostSourceRequest request) {
        return ApiResponse.success(update.execute(
                projectId,
                sourceId,
                request.sourceType(),
                request.sourceId(),
                request.effortHours(),
                request.rateAmount(),
                request.amount(),
                request.currency(),
                request.includedInForecast() == null || request.includedInForecast()));
    }

    @PostMapping("/{sourceId}/archive")
    @Operation(summary = "Archive cost source")
    public ApiResponse<ProfitCostSourceResponse> archive(
            @PathVariable UUID projectId,
            @PathVariable UUID sourceId) {
        return ApiResponse.success(archive.execute(projectId, sourceId));
    }

    @GetMapping
    @Operation(summary = "List cost sources")
    public ApiResponse<List<ProfitCostSourceResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{sourceId}")
    @Operation(summary = "Get cost source")
    public ApiResponse<ProfitCostSourceResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID sourceId) {
        return ApiResponse.success(query.get(projectId, sourceId));
    }
}
