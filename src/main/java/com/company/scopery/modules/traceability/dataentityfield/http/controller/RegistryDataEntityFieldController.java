package com.company.scopery.modules.traceability.dataentityfield.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.dataentityfield.application.action.CreateRegistryDataEntityFieldAction;
import com.company.scopery.modules.traceability.dataentityfield.application.action.DeleteRegistryDataEntityFieldAction;
import com.company.scopery.modules.traceability.dataentityfield.application.action.UpdateRegistryDataEntityFieldAction;
import com.company.scopery.modules.traceability.dataentityfield.application.command.CreateRegistryDataEntityFieldCommand;
import com.company.scopery.modules.traceability.dataentityfield.application.command.UpdateRegistryDataEntityFieldCommand;
import com.company.scopery.modules.traceability.dataentityfield.application.response.RegistryDataEntityFieldResponse;
import com.company.scopery.modules.traceability.dataentityfield.application.service.RegistryDataEntityFieldQueryService;
import com.company.scopery.modules.traceability.dataentityfield.http.request.CreateRegistryDataEntityFieldRequest;
import com.company.scopery.modules.traceability.dataentityfield.http.request.UpdateRegistryDataEntityFieldRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.DATA_ENTITY_FIELDS)
@Tag(name = "Traceability - Data Entity Fields")
public class RegistryDataEntityFieldController {

    private final CreateRegistryDataEntityFieldAction create;
    private final UpdateRegistryDataEntityFieldAction update;
    private final DeleteRegistryDataEntityFieldAction delete;
    private final RegistryDataEntityFieldQueryService query;

    public RegistryDataEntityFieldController(CreateRegistryDataEntityFieldAction create,
                                             UpdateRegistryDataEntityFieldAction update,
                                             DeleteRegistryDataEntityFieldAction delete,
                                             RegistryDataEntityFieldQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create data entity field")
    public ApiResponse<RegistryDataEntityFieldResponse> create(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID entityId,
                                                               @Valid @RequestBody CreateRegistryDataEntityFieldRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryDataEntityFieldCommand(
                entityId, workspaceId, r.columnName(), r.dataType(), r.maxLength(),
                r.isNullable(), r.isUnique(), r.isPrimaryKey(), r.defaultValue(),
                r.precision(), r.scale(), r.remark(), r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List data entity fields")
    public ApiResponse<List<RegistryDataEntityFieldResponse>> list(@PathVariable UUID workspaceId,
                                                                   @PathVariable UUID entityId) {
        return ApiResponse.success(query.list(workspaceId, entityId));
    }

    @GetMapping("/{fieldId}")
    @Operation(summary = "Get data entity field")
    public ApiResponse<RegistryDataEntityFieldResponse> get(@PathVariable UUID workspaceId,
                                                            @PathVariable UUID fieldId) {
        return ApiResponse.success(query.get(workspaceId, fieldId));
    }

    @PutMapping("/{fieldId}")
    @Operation(summary = "Update data entity field")
    public ApiResponse<RegistryDataEntityFieldResponse> update(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID entityId,
                                                               @PathVariable UUID fieldId,
                                                               @Valid @RequestBody UpdateRegistryDataEntityFieldRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryDataEntityFieldCommand(
                workspaceId, fieldId, r.columnName(), r.dataType(), r.maxLength(),
                r.isNullable(), r.isUnique(), r.isPrimaryKey(), r.defaultValue(),
                r.precision(), r.scale(), r.remark(), r.displayOrder())));
    }

    @DeleteMapping("/{fieldId}")
    @Operation(summary = "Delete data entity field")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                    @PathVariable UUID entityId,
                                    @PathVariable UUID fieldId) {
        delete.execute(workspaceId, fieldId);
        return ApiResponse.success(null);
    }
}
