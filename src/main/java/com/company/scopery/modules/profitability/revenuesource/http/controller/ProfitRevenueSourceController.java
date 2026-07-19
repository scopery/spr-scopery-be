package com.company.scopery.modules.profitability.revenuesource.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.revenuesource.application.action.ArchiveProfitRevenueSourceAction;
import com.company.scopery.modules.profitability.revenuesource.application.action.CreateProfitRevenueSourceAction;
import com.company.scopery.modules.profitability.revenuesource.application.action.UpdateProfitRevenueSourceAction;
import com.company.scopery.modules.profitability.revenuesource.application.response.ProfitRevenueSourceResponse;
import com.company.scopery.modules.profitability.revenuesource.application.service.ProfitRevenueSourceQueryService;
import com.company.scopery.modules.profitability.revenuesource.http.request.CreateRevenueSourceRequest;
import com.company.scopery.modules.profitability.revenuesource.http.request.UpdateRevenueSourceRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.REVENUE_SOURCES)
@Tag(name = "Profitability - Revenue Sources")
public class ProfitRevenueSourceController {
    private final CreateProfitRevenueSourceAction create;
    private final UpdateProfitRevenueSourceAction update;
    private final ArchiveProfitRevenueSourceAction archive;
    private final ProfitRevenueSourceQueryService query;

    public ProfitRevenueSourceController(CreateProfitRevenueSourceAction create,
                                         UpdateProfitRevenueSourceAction update,
                                         ArchiveProfitRevenueSourceAction archive,
                                         ProfitRevenueSourceQueryService query) {
        this.create = create;
        this.update = update;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create revenue source")
    public ApiResponse<ProfitRevenueSourceResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateRevenueSourceRequest request) {
        return ApiResponse.success(create.execute(
                projectId,
                request.sourceType(),
                request.sourceId(),
                request.amount(),
                request.currency(),
                request.includedInForecast() == null || request.includedInForecast(),
                request.confidence()));
    }

    @PutMapping("/{sourceId}")
    @Operation(summary = "Update revenue source")
    public ApiResponse<ProfitRevenueSourceResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID sourceId,
            @Valid @RequestBody UpdateRevenueSourceRequest request) {
        return ApiResponse.success(update.execute(
                projectId,
                sourceId,
                request.sourceType(),
                request.sourceId(),
                request.amount(),
                request.currency(),
                request.includedInForecast() == null || request.includedInForecast(),
                request.confidence()));
    }

    @PostMapping("/{sourceId}/archive")
    @Operation(summary = "Archive revenue source")
    public ApiResponse<ProfitRevenueSourceResponse> archive(
            @PathVariable UUID projectId,
            @PathVariable UUID sourceId) {
        return ApiResponse.success(archive.execute(projectId, sourceId));
    }

    @GetMapping
    @Operation(summary = "List revenue sources")
    public ApiResponse<List<ProfitRevenueSourceResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.list(projectId));
    }

    @GetMapping("/{sourceId}")
    @Operation(summary = "Get revenue source")
    public ApiResponse<ProfitRevenueSourceResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID sourceId) {
        return ApiResponse.success(query.get(projectId, sourceId));
    }
}
