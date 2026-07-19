package com.company.scopery.modules.scope.scopeitem.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.scope.scopeitem.application.action.ArchiveScopeItemAction;
import com.company.scopery.modules.scope.scopeitem.application.action.CreateScopeItemAction;
import com.company.scopery.modules.scope.scopeitem.application.action.UpdateScopeItemAction;
import com.company.scopery.modules.scope.scopeitem.application.command.ArchiveScopeItemCommand;
import com.company.scopery.modules.scope.scopeitem.application.command.CreateScopeItemCommand;
import com.company.scopery.modules.scope.scopeitem.application.command.UpdateScopeItemCommand;
import com.company.scopery.modules.scope.scopeitem.application.response.ScopeItemResponse;
import com.company.scopery.modules.scope.scopeitem.application.service.ScopeItemQueryService;
import com.company.scopery.modules.scope.scopeitem.http.request.CreateScopeItemRequest;
import com.company.scopery.modules.scope.scopeitem.http.request.UpdateScopeItemRequest;
import com.company.scopery.modules.scope.shared.constant.ScopeApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Scope - Items")
public class ScopeItemController {
    private final CreateScopeItemAction create;
    private final UpdateScopeItemAction update;
    private final ArchiveScopeItemAction archive;
    private final ScopeItemQueryService query;

    public ScopeItemController(CreateScopeItemAction create, UpdateScopeItemAction update,
                               ArchiveScopeItemAction archive, ScopeItemQueryService query) {
        this.create = create;
        this.update = update;
        this.archive = archive;
        this.query = query;
    }

    @PostMapping(ScopeApiPaths.PACKAGES + "/{packageId}/items")
    @Operation(summary = "Create scope item")
    public ApiResponse<ScopeItemResponse> create(@PathVariable UUID projectId,
                                                 @PathVariable UUID packageId,
                                                 @Valid @RequestBody CreateScopeItemRequest request) {
        return ApiResponse.success(create.execute(new CreateScopeItemCommand(
                projectId, packageId, request.type(), request.code(), request.title(), request.description(),
                request.inScope(), request.outOfScope(), request.priority(), request.acceptanceRequired(),
                request.sortOrder())));
    }

    @GetMapping(ScopeApiPaths.PACKAGES + "/{packageId}/items")
    @Operation(summary = "List package scope items")
    public ApiResponse<List<ScopeItemResponse>> list(@PathVariable UUID projectId, @PathVariable UUID packageId) {
        return ApiResponse.success(query.listByPackage(projectId, packageId));
    }

    @GetMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}")
    @Operation(summary = "Get scope item")
    public ApiResponse<ScopeItemResponse> get(@PathVariable UUID projectId, @PathVariable UUID scopeItemId) {
        return ApiResponse.success(query.get(projectId, scopeItemId));
    }

    @PutMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}")
    @Operation(summary = "Update scope item")
    public ApiResponse<ScopeItemResponse> update(@PathVariable UUID projectId,
                                                 @PathVariable UUID scopeItemId,
                                                 @Valid @RequestBody UpdateScopeItemRequest request) {
        return ApiResponse.success(update.execute(new UpdateScopeItemCommand(
                projectId, scopeItemId, request.title(), request.description(),
                request.inScope(), request.outOfScope(), request.priority(),
                request.acceptanceRequired(), request.sortOrder())));
    }

    @PatchMapping(ScopeApiPaths.ITEMS + "/{scopeItemId}/archive")
    @Operation(summary = "Archive scope item")
    public ApiResponse<ScopeItemResponse> archive(@PathVariable UUID projectId, @PathVariable UUID scopeItemId) {
        return ApiResponse.success(archive.execute(new ArchiveScopeItemCommand(projectId, scopeItemId)));
    }
}
