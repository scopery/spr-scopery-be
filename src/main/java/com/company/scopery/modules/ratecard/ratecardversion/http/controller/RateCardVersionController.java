package com.company.scopery.modules.ratecard.ratecardversion.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.ratecard.ratecardversion.application.action.*;
import com.company.scopery.modules.ratecard.ratecardversion.application.command.*;
import com.company.scopery.modules.ratecard.ratecardversion.application.response.RateCardVersionResponse;
import com.company.scopery.modules.ratecard.ratecardversion.application.service.RateCardVersionQueryService;
import com.company.scopery.modules.ratecard.ratecardversion.http.request.CreateRateCardVersionRequest;
import com.company.scopery.modules.ratecard.ratecardversion.http.request.UpdateRateCardVersionRequest;
import com.company.scopery.modules.ratecard.shared.constant.RateCardApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RateCardApiPaths.CARD_VERSIONS)
@Tag(name = "Rate Card - Versions")
public class RateCardVersionController {
    private final RateCardVersionQueryService queryService;
    private final CreateRateCardVersionAction createAction;
    private final UpdateRateCardVersionAction updateAction;
    private final PublishRateCardVersionAction publishAction;
    private final ArchiveRateCardVersionAction archiveAction;
    private final DuplicateRateCardVersionAction duplicateAction;

    public RateCardVersionController(RateCardVersionQueryService queryService,
                                     CreateRateCardVersionAction createAction,
                                     UpdateRateCardVersionAction updateAction,
                                     PublishRateCardVersionAction publishAction,
                                     ArchiveRateCardVersionAction archiveAction,
                                     DuplicateRateCardVersionAction duplicateAction) {
        this.queryService = queryService; this.createAction = createAction; this.updateAction = updateAction;
        this.publishAction = publishAction; this.archiveAction = archiveAction; this.duplicateAction = duplicateAction;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Create rate card version")
    public ApiResponse<RateCardVersionResponse> create(@PathVariable UUID rateCardId,
                                                       @Valid @RequestBody CreateRateCardVersionRequest request) {
        return ApiResponse.success(createAction.execute(new CreateRateCardVersionCommand(
                rateCardId, request.name(), request.description(), request.effectiveFrom(), request.effectiveTo())));
    }

    @GetMapping @Operation(summary = "List rate card versions")
    public ApiResponse<List<RateCardVersionResponse>> list(@PathVariable UUID rateCardId) {
        return ApiResponse.success(queryService.list(rateCardId));
    }

    @GetMapping("/{versionId}") @Operation(summary = "Get rate card version")
    public ApiResponse<RateCardVersionResponse> get(@PathVariable UUID rateCardId, @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.get(rateCardId, versionId));
    }

    @PutMapping("/{versionId}") @Operation(summary = "Update draft rate card version")
    public ApiResponse<RateCardVersionResponse> update(@PathVariable UUID rateCardId, @PathVariable UUID versionId,
                                                       @Valid @RequestBody UpdateRateCardVersionRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateRateCardVersionCommand(
                rateCardId, versionId, request.name(), request.description(),
                request.effectiveFrom(), request.effectiveTo())));
    }

    @PostMapping("/{versionId}/publish") @Operation(summary = "Publish rate card version")
    public ApiResponse<RateCardVersionResponse> publish(@PathVariable UUID rateCardId, @PathVariable UUID versionId) {
        return ApiResponse.success(publishAction.execute(new PublishRateCardVersionCommand(rateCardId, versionId)));
    }

    @PatchMapping("/{versionId}/archive") @Operation(summary = "Archive rate card version")
    public ApiResponse<RateCardVersionResponse> archive(@PathVariable UUID rateCardId, @PathVariable UUID versionId) {
        return ApiResponse.success(archiveAction.execute(new ArchiveRateCardVersionCommand(rateCardId, versionId)));
    }

    @PostMapping("/{versionId}/duplicate") @Operation(summary = "Duplicate rate card version")
    public ApiResponse<RateCardVersionResponse> duplicate(@PathVariable UUID rateCardId, @PathVariable UUID versionId) {
        return ApiResponse.success(duplicateAction.execute(new DuplicateRateCardVersionCommand(rateCardId, versionId)));
    }
}
