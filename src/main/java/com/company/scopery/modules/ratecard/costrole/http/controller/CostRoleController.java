package com.company.scopery.modules.ratecard.costrole.http.controller;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.ratecard.costrole.application.action.ActivateCostRoleAction;
import com.company.scopery.modules.ratecard.costrole.application.action.ArchiveCostRoleAction;
import com.company.scopery.modules.ratecard.costrole.application.action.CreateCostRoleAction;
import com.company.scopery.modules.ratecard.costrole.application.action.DeactivateCostRoleAction;
import com.company.scopery.modules.ratecard.costrole.application.action.UpdateCostRoleAction;
import com.company.scopery.modules.ratecard.costrole.application.command.ActivateCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.command.ArchiveCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.command.CreateCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.command.DeactivateCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.command.UpdateCostRoleCommand;
import com.company.scopery.modules.ratecard.costrole.application.query.SearchCostRoleQuery;
import com.company.scopery.modules.ratecard.costrole.application.response.CostRoleResponse;
import com.company.scopery.modules.ratecard.costrole.application.service.CostRoleQueryService;
import com.company.scopery.modules.ratecard.costrole.http.request.CreateCostRoleRequest;
import com.company.scopery.modules.ratecard.costrole.http.request.UpdateCostRoleRequest;
import com.company.scopery.modules.ratecard.shared.constant.RateCardApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(RateCardApiPaths.COST_ROLES)
@Tag(name = "Rate Card - Cost Roles")
public class CostRoleController {

    private final CostRoleQueryService queryService;
    private final CreateCostRoleAction createAction;
    private final UpdateCostRoleAction updateAction;
    private final ActivateCostRoleAction activateAction;
    private final DeactivateCostRoleAction deactivateAction;
    private final ArchiveCostRoleAction archiveAction;

    public CostRoleController(CostRoleQueryService queryService,
                              CreateCostRoleAction createAction,
                              UpdateCostRoleAction updateAction,
                              ActivateCostRoleAction activateAction,
                              DeactivateCostRoleAction deactivateAction,
                              ArchiveCostRoleAction archiveAction) {
        this.queryService = queryService;
        this.createAction = createAction;
        this.updateAction = updateAction;
        this.activateAction = activateAction;
        this.deactivateAction = deactivateAction;
        this.archiveAction = archiveAction;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a cost role")
    public ApiResponse<CostRoleResponse> create(@Valid @RequestBody CreateCostRoleRequest request) {
        return ApiResponse.success(createAction.execute(new CreateCostRoleCommand(
                request.code(), request.name(), request.description(), request.scope(),
                request.organizationId(), request.workspaceId(), request.category())));
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Get a cost role by ID")
    public ApiResponse<CostRoleResponse> get(@PathVariable UUID roleId) {
        return ApiResponse.success(queryService.get(roleId));
    }

    @GetMapping
    @Operation(summary = "Search cost roles")
    public ApiResponse<PageResponse<CostRoleResponse>> search(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID workspaceId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<CostRoleResponse> result = queryService.search(
                new SearchCostRoleQuery(scope, organizationId, workspaceId, status, category, code, page, size));
        return ApiResponse.success(PageResponse.fromDomain(result));
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Update a cost role")
    public ApiResponse<CostRoleResponse> update(@PathVariable UUID roleId,
                                                @Valid @RequestBody UpdateCostRoleRequest request) {
        return ApiResponse.success(updateAction.execute(
                new UpdateCostRoleCommand(roleId, request.name(), request.description(), request.category())));
    }

    @PatchMapping("/{roleId}/activate")
    @Operation(summary = "Activate a cost role")
    public ApiResponse<CostRoleResponse> activate(@PathVariable UUID roleId) {
        return ApiResponse.success(activateAction.execute(new ActivateCostRoleCommand(roleId)));
    }

    @PatchMapping("/{roleId}/deactivate")
    @Operation(summary = "Deactivate a cost role")
    public ApiResponse<CostRoleResponse> deactivate(@PathVariable UUID roleId) {
        return ApiResponse.success(deactivateAction.execute(new DeactivateCostRoleCommand(roleId)));
    }

    @PatchMapping("/{roleId}/archive")
    @Operation(summary = "Archive a cost role")
    public ApiResponse<CostRoleResponse> archive(@PathVariable UUID roleId) {
        return ApiResponse.success(archiveAction.execute(new ArchiveCostRoleCommand(roleId)));
    }
}
