package com.company.scopery.modules.traceability.componentfield.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.componentfield.application.action.CreateRegistryComponentFieldAction;
import com.company.scopery.modules.traceability.componentfield.application.action.DeleteRegistryComponentFieldAction;
import com.company.scopery.modules.traceability.componentfield.application.action.UpdateRegistryComponentFieldAction;
import com.company.scopery.modules.traceability.componentfield.application.command.CreateRegistryComponentFieldCommand;
import com.company.scopery.modules.traceability.componentfield.application.command.UpdateRegistryComponentFieldCommand;
import com.company.scopery.modules.traceability.componentfield.application.response.RegistryComponentFieldResponse;
import com.company.scopery.modules.traceability.componentfield.application.service.RegistryComponentFieldQueryService;
import com.company.scopery.modules.traceability.componentfield.http.request.CreateRegistryComponentFieldRequest;
import com.company.scopery.modules.traceability.componentfield.http.request.UpdateRegistryComponentFieldRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.COMPONENT_FIELDS)
@Tag(name = "Traceability - Component Fields")
public class RegistryComponentFieldController {

    private final CreateRegistryComponentFieldAction create;
    private final UpdateRegistryComponentFieldAction update;
    private final DeleteRegistryComponentFieldAction delete;
    private final RegistryComponentFieldQueryService query;

    public RegistryComponentFieldController(CreateRegistryComponentFieldAction create,
                                             UpdateRegistryComponentFieldAction update,
                                             DeleteRegistryComponentFieldAction delete,
                                             RegistryComponentFieldQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Add field to component")
    public ApiResponse<RegistryComponentFieldResponse> create(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID componentId,
                                                               @Valid @RequestBody CreateRegistryComponentFieldRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryComponentFieldCommand(
                workspaceId, componentId, r.fieldKey(), r.label(), r.fieldType(),
                Boolean.TRUE.equals(r.required()), r.maxLength(), r.remark(),
                r.displayOrder() != null ? r.displayOrder() : 0)));
    }

    @GetMapping
    @Operation(summary = "List component fields")
    public ApiResponse<List<RegistryComponentFieldResponse>> list(@PathVariable UUID workspaceId,
                                                                   @PathVariable UUID componentId) {
        return ApiResponse.success(query.list(workspaceId, componentId));
    }

    @GetMapping("/{fieldId}")
    @Operation(summary = "Get component field")
    public ApiResponse<RegistryComponentFieldResponse> get(@PathVariable UUID workspaceId,
                                                            @PathVariable UUID fieldId) {
        return ApiResponse.success(query.get(workspaceId, fieldId));
    }

    @PutMapping("/{fieldId}")
    @Operation(summary = "Update component field")
    public ApiResponse<RegistryComponentFieldResponse> update(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID componentId,
                                                               @PathVariable UUID fieldId,
                                                               @Valid @RequestBody UpdateRegistryComponentFieldRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryComponentFieldCommand(
                workspaceId, fieldId, r.label(), r.fieldType(),
                Boolean.TRUE.equals(r.required()), r.maxLength(), r.remark(),
                r.displayOrder() != null ? r.displayOrder() : 0)));
    }

    @DeleteMapping("/{fieldId}")
    @Operation(summary = "Delete component field")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                     @PathVariable UUID componentId,
                                     @PathVariable UUID fieldId) {
        delete.execute(workspaceId, fieldId);
        return ApiResponse.success(null);
    }
}
