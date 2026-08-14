package com.company.scopery.modules.traceability.componentoption.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.componentoption.application.action.CreateRegistryComponentOptionAction;
import com.company.scopery.modules.traceability.componentoption.application.action.DeleteRegistryComponentOptionAction;
import com.company.scopery.modules.traceability.componentoption.application.action.UpdateRegistryComponentOptionAction;
import com.company.scopery.modules.traceability.componentoption.application.command.CreateRegistryComponentOptionCommand;
import com.company.scopery.modules.traceability.componentoption.application.command.UpdateRegistryComponentOptionCommand;
import com.company.scopery.modules.traceability.componentoption.application.response.RegistryComponentOptionResponse;
import com.company.scopery.modules.traceability.componentoption.application.service.RegistryComponentOptionQueryService;
import com.company.scopery.modules.traceability.componentoption.http.request.CreateRegistryComponentOptionRequest;
import com.company.scopery.modules.traceability.componentoption.http.request.UpdateRegistryComponentOptionRequest;
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
@RequestMapping(TraceabilityApiPaths.COMPONENT_OPTIONS)
@Tag(name = "Traceability - Component Options")
public class RegistryComponentOptionController {

    private final CreateRegistryComponentOptionAction create;
    private final UpdateRegistryComponentOptionAction update;
    private final DeleteRegistryComponentOptionAction delete;
    private final RegistryComponentOptionQueryService query;

    public RegistryComponentOptionController(CreateRegistryComponentOptionAction create,
                                              UpdateRegistryComponentOptionAction update,
                                              DeleteRegistryComponentOptionAction delete,
                                              RegistryComponentOptionQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Create component option")
    public ApiResponse<RegistryComponentOptionResponse> create(
            @PathVariable UUID workspaceId,
            @PathVariable UUID componentId,
            @Valid @RequestBody CreateRegistryComponentOptionRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryComponentOptionCommand(
                workspaceId, componentId, r.optionValue(), r.optionLabel(), r.displayOrder())));
    }

    @GetMapping
    @Operation(summary = "List component options")
    public ApiResponse<List<RegistryComponentOptionResponse>> list(
            @PathVariable UUID workspaceId,
            @PathVariable UUID componentId) {
        return ApiResponse.success(query.listByComponent(workspaceId, componentId));
    }

    @GetMapping("/{optionId}")
    @Operation(summary = "Get component option by ID")
    public ApiResponse<RegistryComponentOptionResponse> get(
            @PathVariable UUID workspaceId,
            @PathVariable UUID componentId,
            @PathVariable UUID optionId) {
        return ApiResponse.success(query.get(workspaceId, optionId));
    }

    @PutMapping("/{optionId}")
    @Operation(summary = "Update component option")
    public ApiResponse<RegistryComponentOptionResponse> update(
            @PathVariable UUID workspaceId,
            @PathVariable UUID componentId,
            @PathVariable UUID optionId,
            @Valid @RequestBody UpdateRegistryComponentOptionRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryComponentOptionCommand(
                workspaceId, componentId, optionId, r.optionValue(), r.optionLabel(), r.displayOrder())));
    }

    @DeleteMapping("/{optionId}")
    @Operation(summary = "Delete component option")
    public ApiResponse<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID componentId,
            @PathVariable UUID optionId) {
        delete.execute(workspaceId, optionId);
        return ApiResponse.success(null);
    }
}
