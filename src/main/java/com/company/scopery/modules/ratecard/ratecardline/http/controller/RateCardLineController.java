package com.company.scopery.modules.ratecard.ratecardline.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.ratecard.ratecardline.application.action.*;
import com.company.scopery.modules.ratecard.ratecardline.application.command.*;
import com.company.scopery.modules.ratecard.ratecardline.application.response.RateCardLineResponse;
import com.company.scopery.modules.ratecard.ratecardline.application.service.RateCardLineQueryService;
import com.company.scopery.modules.ratecard.ratecardline.http.request.CreateRateCardLineRequest;
import com.company.scopery.modules.ratecard.ratecardline.http.request.UpdateRateCardLineRequest;
import com.company.scopery.modules.ratecard.shared.constant.RateCardApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(RateCardApiPaths.CARD_LINES)
@Tag(name = "Rate Card - Lines")
public class RateCardLineController {
    private final RateCardLineQueryService queryService;
    private final CreateRateCardLineAction createAction;
    private final UpdateRateCardLineAction updateAction;
    private final DeleteRateCardLineAction deleteAction;

    public RateCardLineController(RateCardLineQueryService queryService, CreateRateCardLineAction createAction,
                                  UpdateRateCardLineAction updateAction, DeleteRateCardLineAction deleteAction) {
        this.queryService = queryService; this.createAction = createAction;
        this.updateAction = updateAction; this.deleteAction = deleteAction;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Create rate card line")
    public ApiResponse<RateCardLineResponse> create(@PathVariable UUID rateCardId, @PathVariable UUID versionId,
                                                    @Valid @RequestBody CreateRateCardLineRequest request) {
        return ApiResponse.success(createAction.execute(new CreateRateCardLineCommand(
                rateCardId, versionId, request.costRoleId(), request.seniorityLevel(), request.locationCode(),
                request.currencyCode(), request.costRatePerHour(), request.billingRatePerHour(), request.notes())));
    }

    @GetMapping @Operation(summary = "List rate card lines")
    public ApiResponse<List<RateCardLineResponse>> list(@PathVariable UUID rateCardId, @PathVariable UUID versionId) {
        return ApiResponse.success(queryService.list(rateCardId, versionId));
    }

    @GetMapping("/{lineId}") @Operation(summary = "Get rate card line")
    public ApiResponse<RateCardLineResponse> get(@PathVariable UUID rateCardId, @PathVariable UUID versionId,
                                                 @PathVariable UUID lineId) {
        return ApiResponse.success(queryService.get(rateCardId, versionId, lineId));
    }

    @PutMapping("/{lineId}") @Operation(summary = "Update rate card line")
    public ApiResponse<RateCardLineResponse> update(@PathVariable UUID rateCardId, @PathVariable UUID versionId,
                                                    @PathVariable UUID lineId,
                                                    @Valid @RequestBody UpdateRateCardLineRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateRateCardLineCommand(
                rateCardId, versionId, lineId, request.costRoleId(), request.seniorityLevel(), request.locationCode(),
                request.currencyCode(), request.costRatePerHour(), request.billingRatePerHour(), request.notes())));
    }

    @DeleteMapping("/{lineId}") @ResponseStatus(HttpStatus.NO_CONTENT) @Operation(summary = "Delete rate card line")
    public void delete(@PathVariable UUID rateCardId, @PathVariable UUID versionId, @PathVariable UUID lineId) {
        deleteAction.execute(new DeleteRateCardLineCommand(rateCardId, versionId, lineId));
    }
}
