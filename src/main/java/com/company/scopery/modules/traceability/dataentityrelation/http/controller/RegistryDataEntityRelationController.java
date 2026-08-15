package com.company.scopery.modules.traceability.dataentityrelation.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.dataentityrelation.application.action.CreateRegistryDataEntityRelationAction;
import com.company.scopery.modules.traceability.dataentityrelation.application.action.DeleteRegistryDataEntityRelationAction;
import com.company.scopery.modules.traceability.dataentityrelation.application.action.UpdateRegistryDataEntityRelationAction;
import com.company.scopery.modules.traceability.dataentityrelation.application.command.CreateRegistryDataEntityRelationCommand;
import com.company.scopery.modules.traceability.dataentityrelation.application.command.UpdateRegistryDataEntityRelationCommand;
import com.company.scopery.modules.traceability.dataentityrelation.application.response.RegistryDataEntityRelationResponse;
import com.company.scopery.modules.traceability.dataentityrelation.application.service.RegistryDataEntityRelationQueryService;
import com.company.scopery.modules.traceability.dataentityrelation.http.request.CreateRegistryDataEntityRelationRequest;
import com.company.scopery.modules.traceability.dataentityrelation.http.request.UpdateRegistryDataEntityRelationRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.DATA_ENTITY_RELATIONS)
@Tag(name = "Traceability - Data Entity Relations")
public class RegistryDataEntityRelationController {

    private final CreateRegistryDataEntityRelationAction create;
    private final UpdateRegistryDataEntityRelationAction update;
    private final DeleteRegistryDataEntityRelationAction delete;
    private final RegistryDataEntityRelationQueryService query;

    public RegistryDataEntityRelationController(CreateRegistryDataEntityRelationAction create,
                                                 UpdateRegistryDataEntityRelationAction update,
                                                 DeleteRegistryDataEntityRelationAction delete,
                                                 RegistryDataEntityRelationQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create data entity relation")
    public ApiResponse<RegistryDataEntityRelationResponse> create(@PathVariable UUID workspaceId,
                                                                   @PathVariable UUID entityId,
                                                                   @Valid @RequestBody CreateRegistryDataEntityRelationRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryDataEntityRelationCommand(
                entityId, r.targetEntityId(), workspaceId,
                r.relationType(), r.sourceColumn(), r.label(), r.note())));
    }

    @GetMapping
    @Operation(summary = "List data entity relations")
    public ApiResponse<List<RegistryDataEntityRelationResponse>> list(@PathVariable UUID workspaceId,
                                                                       @PathVariable UUID entityId) {
        return ApiResponse.success(query.list(workspaceId, entityId));
    }

    @GetMapping("/{relationId}")
    @Operation(summary = "Get data entity relation")
    public ApiResponse<RegistryDataEntityRelationResponse> get(@PathVariable UUID workspaceId,
                                                                @PathVariable UUID relationId) {
        return ApiResponse.success(query.get(workspaceId, relationId));
    }

    @PutMapping("/{relationId}")
    @Operation(summary = "Update data entity relation")
    public ApiResponse<RegistryDataEntityRelationResponse> update(@PathVariable UUID workspaceId,
                                                                   @PathVariable UUID entityId,
                                                                   @PathVariable UUID relationId,
                                                                   @Valid @RequestBody UpdateRegistryDataEntityRelationRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryDataEntityRelationCommand(
                workspaceId, relationId, r.relationType(), r.sourceColumn(), r.label(), r.note())));
    }

    @DeleteMapping("/{relationId}")
    @Operation(summary = "Delete data entity relation")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                     @PathVariable UUID entityId,
                                     @PathVariable UUID relationId) {
        delete.execute(workspaceId, relationId);
        return ApiResponse.success(null);
    }
}
