package com.company.scopery.modules.resourcereference.resourcetype.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.resourcereference.resourcetype.application.action.CreateResourceTypeAction;
import com.company.scopery.modules.resourcereference.resourcetype.application.action.DeprecateResourceTypeAction;
import com.company.scopery.modules.resourcereference.resourcetype.application.command.CreateResourceTypeCommand;
import com.company.scopery.modules.resourcereference.resourcetype.application.command.DeprecateResourceTypeCommand;
import com.company.scopery.modules.resourcereference.resourcetype.application.response.ResourceTypeResponse;
import com.company.scopery.modules.resourcereference.resourcetype.application.service.ResourceTypeQueryService;
import com.company.scopery.modules.resourcereference.resourcetype.http.request.CreateResourceTypeRequest;
import com.company.scopery.modules.resourcereference.shared.constant.ResourceReferenceApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Resource Reference - Types")
public class ResourceTypeController {

    private final CreateResourceTypeAction create;
    private final DeprecateResourceTypeAction deprecate;
    private final ResourceTypeQueryService query;

    public ResourceTypeController(CreateResourceTypeAction create,
                                   DeprecateResourceTypeAction deprecate,
                                   ResourceTypeQueryService query) {
        this.create = create;
        this.deprecate = deprecate;
        this.query = query;
    }

    @GetMapping(ResourceReferenceApiPaths.RESOURCE_TYPES)
    @Operation(summary = "List all resource types")
    public ApiResponse<List<ResourceTypeResponse>> list(@RequestParam(defaultValue = "false") boolean enabledOnly) {
        return ApiResponse.success(enabledOnly ? query.listEnabled() : query.listAll());
    }

    @GetMapping(ResourceReferenceApiPaths.RESOURCE_TYPES + "/{id}")
    @Operation(summary = "Get resource type by ID")
    public ApiResponse<ResourceTypeResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(query.get(id));
    }

    @PostMapping(ResourceReferenceApiPaths.RESOURCE_TYPES)
    @Operation(summary = "Create resource type")
    public ApiResponse<ResourceTypeResponse> create(@Valid @RequestBody CreateResourceTypeRequest r) {
        return ApiResponse.success(create.execute(new CreateResourceTypeCommand(r.code(), r.displayName(), r.description(), false)));
    }

    @PostMapping(ResourceReferenceApiPaths.RESOURCE_TYPES + "/{id}/deprecate")
    @Operation(summary = "Deprecate (disable) resource type")
    public ApiResponse<ResourceTypeResponse> deprecate(@PathVariable UUID id) {
        return ApiResponse.success(deprecate.execute(new DeprecateResourceTypeCommand(id)));
    }
}
