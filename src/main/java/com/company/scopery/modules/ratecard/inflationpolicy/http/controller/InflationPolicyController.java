package com.company.scopery.modules.ratecard.inflationpolicy.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.ratecard.inflationpolicy.application.action.*;
import com.company.scopery.modules.ratecard.inflationpolicy.application.command.*;
import com.company.scopery.modules.ratecard.inflationpolicy.application.query.SearchInflationPolicyQuery;
import com.company.scopery.modules.ratecard.inflationpolicy.application.response.InflationPolicyResponse;
import com.company.scopery.modules.ratecard.inflationpolicy.application.service.InflationPolicyQueryService;
import com.company.scopery.modules.ratecard.inflationpolicy.http.request.CreateInflationPolicyRequest;
import com.company.scopery.modules.ratecard.inflationpolicy.http.request.UpdateInflationPolicyRequest;
import com.company.scopery.modules.ratecard.shared.constant.RateCardApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(RateCardApiPaths.INFLATION_POLICIES)
@Tag(name = "Rate Card - Inflation Policies")
public class InflationPolicyController {
    private final InflationPolicyQueryService queryService;
    private final CreateInflationPolicyAction createAction;
    private final UpdateInflationPolicyAction updateAction;
    private final ActivateInflationPolicyAction activateAction;
    private final DeactivateInflationPolicyAction deactivateAction;
    private final ArchiveInflationPolicyAction archiveAction;

    public InflationPolicyController(InflationPolicyQueryService queryService, CreateInflationPolicyAction createAction,
                                     UpdateInflationPolicyAction updateAction, ActivateInflationPolicyAction activateAction,
                                     DeactivateInflationPolicyAction deactivateAction, ArchiveInflationPolicyAction archiveAction) {
        this.queryService = queryService; this.createAction = createAction; this.updateAction = updateAction;
        this.activateAction = activateAction; this.deactivateAction = deactivateAction; this.archiveAction = archiveAction;
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Create inflation policy")
    public ApiResponse<InflationPolicyResponse> create(@Valid @RequestBody CreateInflationPolicyRequest request) {
        return ApiResponse.success(createAction.execute(new CreateInflationPolicyCommand(
                request.code(), request.name(), request.description(), request.scope(),
                request.organizationId(), request.workspaceId(), request.inflationPercent(),
                request.compoundFrequency(), request.effectiveFrom(), request.effectiveTo())));
    }

    @GetMapping("/{policyId}") public ApiResponse<InflationPolicyResponse> get(@PathVariable UUID policyId) {
        return ApiResponse.success(queryService.get(policyId));
    }

    @GetMapping public ApiResponse<PageResponse<InflationPolicyResponse>> search(
            @RequestParam(required = false) String scope, @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID workspaceId, @RequestParam(required = false) String status,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.fromDomain(queryService.search(
                new SearchInflationPolicyQuery(scope, organizationId, workspaceId, status, code, page, size))));
    }

    @PutMapping("/{policyId}") public ApiResponse<InflationPolicyResponse> update(
            @PathVariable UUID policyId, @Valid @RequestBody UpdateInflationPolicyRequest request) {
        return ApiResponse.success(updateAction.execute(new UpdateInflationPolicyCommand(
                policyId, request.name(), request.description(), request.inflationPercent(),
                request.compoundFrequency(), request.effectiveFrom(), request.effectiveTo())));
    }

    @PatchMapping("/{policyId}/activate") public ApiResponse<InflationPolicyResponse> activate(@PathVariable UUID policyId) {
        return ApiResponse.success(activateAction.execute(new ActivateInflationPolicyCommand(policyId)));
    }
    @PatchMapping("/{policyId}/deactivate") public ApiResponse<InflationPolicyResponse> deactivate(@PathVariable UUID policyId) {
        return ApiResponse.success(deactivateAction.execute(new DeactivateInflationPolicyCommand(policyId)));
    }
    @PatchMapping("/{policyId}/archive") public ApiResponse<InflationPolicyResponse> archive(@PathVariable UUID policyId) {
        return ApiResponse.success(archiveAction.execute(new ArchiveInflationPolicyCommand(policyId)));
    }
}
