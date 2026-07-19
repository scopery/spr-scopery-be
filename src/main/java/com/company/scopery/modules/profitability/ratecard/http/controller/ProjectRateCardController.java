package com.company.scopery.modules.profitability.ratecard.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.profitability.ratecard.application.action.ArchiveRateCardAction;
import com.company.scopery.modules.profitability.ratecard.application.action.CreateRateCardAction;
import com.company.scopery.modules.profitability.ratecard.application.action.UpdateRateCardAction;
import com.company.scopery.modules.profitability.ratecard.application.response.ProfitRateCardResponse;
import com.company.scopery.modules.profitability.ratecard.application.service.ProfitRateCardQueryService;
import com.company.scopery.modules.profitability.ratecard.http.request.CreateRateCardRequest;
import com.company.scopery.modules.profitability.ratecard.http.request.UpdateRateCardRequest;
import com.company.scopery.modules.profitability.shared.constant.ProfitabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ProfitabilityApiPaths.PROJECT_RATE_CARDS)
@Tag(name = "Profitability - Rate Cards")
public class ProjectRateCardController {
    private final CreateRateCardAction create;
    private final UpdateRateCardAction update;
    private final ArchiveRateCardAction archive;
    private final ProfitRateCardQueryService query;

    public ProjectRateCardController(CreateRateCardAction create,
                                     UpdateRateCardAction update,
                                     ArchiveRateCardAction archive,
                                     ProfitRateCardQueryService query) {
        this.create = create;
        this.update = update;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create project rate card")
    public ApiResponse<ProfitRateCardResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateRateCardRequest request) {
        // We need workspaceId; for project-scoped rate cards authorization is via projectId.
        // workspaceId is set to null here and resolved from profile if needed — actions use authorization.requireUpdate(projectId)
        return ApiResponse.success(create.execute(
                null, projectId,
                request.rateCode(), request.name(), request.rateType(),
                request.roleName(), request.teamId(), request.currency(),
                request.amountPerHour(), request.amountPerDay()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project rate card")
    public ApiResponse<ProfitRateCardResponse> update(
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRateCardRequest request) {
        return ApiResponse.success(update.execute(
                null, id,
                request.name(), request.roleName(), request.currency(),
                request.amountPerHour(), request.amountPerDay()));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive project rate card")
    public ApiResponse<ProfitRateCardResponse> archive(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(archive.execute(null, id));
    }

    @GetMapping
    @Operation(summary = "List project rate cards")
    public ApiResponse<List<ProfitRateCardResponse>> list(@PathVariable UUID projectId) {
        return ApiResponse.success(query.listByProject(projectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rate card")
    public ApiResponse<ProfitRateCardResponse> get(
            @PathVariable UUID projectId,
            @PathVariable UUID id) {
        return ApiResponse.success(query.get(id));
    }
}
