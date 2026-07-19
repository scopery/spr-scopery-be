package com.company.scopery.modules.ratecard.ratecard.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.ratecard.ratecard.application.action.*;
import com.company.scopery.modules.ratecard.ratecard.application.command.*;
import com.company.scopery.modules.ratecard.ratecard.application.query.SearchRateCardQuery;
import com.company.scopery.modules.ratecard.ratecard.application.response.RateCardResponse;
import com.company.scopery.modules.ratecard.ratecard.application.service.RateCardQueryService;
import com.company.scopery.modules.ratecard.ratecard.http.request.CreateRateCardRequest;
import com.company.scopery.modules.ratecard.ratecard.http.request.UpdateRateCardRequest;
import com.company.scopery.modules.ratecard.shared.constant.RateCardApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(RateCardApiPaths.CARDS)
@Tag(name = "Rate Card - Cards")
public class RateCardController {
    private final RateCardQueryService queryService;
    private final CreateRateCardAction createAction;
    private final UpdateRateCardAction updateAction;
    private final ActivateRateCardAction activateAction;
    private final DeactivateRateCardAction deactivateAction;
    private final ArchiveRateCardAction archiveAction;

    public RateCardController(RateCardQueryService queryService, CreateRateCardAction createAction,
                              UpdateRateCardAction updateAction, ActivateRateCardAction activateAction,
                              DeactivateRateCardAction deactivateAction, ArchiveRateCardAction archiveAction) {
        this.queryService = queryService; this.createAction = createAction; this.updateAction = updateAction;
        this.activateAction = activateAction; this.deactivateAction = deactivateAction; this.archiveAction = archiveAction;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Create a rate card")
    public ApiResponse<RateCardResponse> create(@Valid @RequestBody CreateRateCardRequest request) {
        return ApiResponse.success(createAction.execute(new CreateRateCardCommand(
                request.code(), request.name(), request.description(), request.scope(),
                request.organizationId(), request.workspaceId(), request.clientId(), request.projectId(),
                request.defaultCurrencyCode(), request.isDefault())));
    }

    @GetMapping("/{rateCardId}") @Operation(summary = "Get rate card")
    public ApiResponse<RateCardResponse> get(@PathVariable UUID rateCardId) {
        return ApiResponse.success(queryService.get(rateCardId));
    }

    @GetMapping @Operation(summary = "Search rate cards")
    public ApiResponse<PageResponse<RateCardResponse>> search(
            @RequestParam(required = false) String scope, @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID workspaceId, @RequestParam(required = false) String status,
            @RequestParam(required = false) String currency, @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.fromDomain(queryService.search(
                new SearchRateCardQuery(scope, organizationId, workspaceId, status, currency, code, page, size))));
    }

    @PutMapping("/{rateCardId}") @Operation(summary = "Update rate card")
    public ApiResponse<RateCardResponse> update(@PathVariable UUID rateCardId, @Valid @RequestBody UpdateRateCardRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateRateCardCommand(
                rateCardId, request.name(), request.description(), request.defaultCurrencyCode())));
    }

    @PatchMapping("/{rateCardId}/activate") public ApiResponse<RateCardResponse> activate(@PathVariable UUID rateCardId) {
        return ApiResponse.success(activateAction.execute(new ActivateRateCardCommand(rateCardId)));
    }
    @PatchMapping("/{rateCardId}/deactivate") public ApiResponse<RateCardResponse> deactivate(@PathVariable UUID rateCardId) {
        return ApiResponse.success(deactivateAction.execute(new DeactivateRateCardCommand(rateCardId)));
    }
    @PatchMapping("/{rateCardId}/archive") public ApiResponse<RateCardResponse> archive(@PathVariable UUID rateCardId) {
        return ApiResponse.success(archiveAction.execute(new ArchiveRateCardCommand(rateCardId)));
    }
}
