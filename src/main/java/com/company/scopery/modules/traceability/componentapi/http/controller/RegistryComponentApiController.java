package com.company.scopery.modules.traceability.componentapi.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.componentapi.application.action.CreateRegistryComponentApiAction;
import com.company.scopery.modules.traceability.componentapi.application.action.DeleteRegistryComponentApiAction;
import com.company.scopery.modules.traceability.componentapi.application.action.UpdateRegistryComponentApiAction;
import com.company.scopery.modules.traceability.componentapi.application.command.CreateRegistryComponentApiCommand;
import com.company.scopery.modules.traceability.componentapi.application.command.UpdateRegistryComponentApiCommand;
import com.company.scopery.modules.traceability.componentapi.application.response.RegistryComponentApiResponse;
import com.company.scopery.modules.traceability.componentapi.application.service.RegistryComponentApiQueryService;
import com.company.scopery.modules.traceability.componentapi.http.request.CreateRegistryComponentApiRequest;
import com.company.scopery.modules.traceability.componentapi.http.request.UpdateRegistryComponentApiRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.COMPONENT_API)
@Tag(name = "Traceability - Component APIs")
public class RegistryComponentApiController {

    private final CreateRegistryComponentApiAction create;
    private final UpdateRegistryComponentApiAction update;
    private final DeleteRegistryComponentApiAction delete;
    private final RegistryComponentApiQueryService query;

    public RegistryComponentApiController(CreateRegistryComponentApiAction create,
                                           UpdateRegistryComponentApiAction update,
                                           DeleteRegistryComponentApiAction delete,
                                           RegistryComponentApiQueryService query) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
    }

    @PostMapping
    @Operation(summary = "Link an API to a component")
    public ApiResponse<RegistryComponentApiResponse> create(@PathVariable UUID workspaceId,
                                                             @PathVariable UUID componentId,
                                                             @Valid @RequestBody CreateRegistryComponentApiRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryComponentApiCommand(
                workspaceId, componentId, r.apiId(), r.role(), r.note(),
                r.displayOrder() != null ? r.displayOrder() : 0)));
    }

    @GetMapping
    @Operation(summary = "List APIs linked to a component")
    public ApiResponse<List<RegistryComponentApiResponse>> list(@PathVariable UUID workspaceId,
                                                                  @PathVariable UUID componentId) {
        return ApiResponse.success(query.list(workspaceId, componentId));
    }

    @PutMapping("/{apiLinkId}")
    @Operation(summary = "Update a component API link")
    public ApiResponse<RegistryComponentApiResponse> update(@PathVariable UUID workspaceId,
                                                             @PathVariable UUID componentId,
                                                             @PathVariable UUID apiLinkId,
                                                             @Valid @RequestBody UpdateRegistryComponentApiRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryComponentApiCommand(
                workspaceId, apiLinkId, r.role(), r.note(),
                r.displayOrder() != null ? r.displayOrder() : 0)));
    }

    @DeleteMapping("/{apiLinkId}")
    @Operation(summary = "Remove an API link from a component")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                     @PathVariable UUID componentId,
                                     @PathVariable UUID apiLinkId) {
        delete.execute(workspaceId, apiLinkId);
        return ApiResponse.success(null);
    }
}
