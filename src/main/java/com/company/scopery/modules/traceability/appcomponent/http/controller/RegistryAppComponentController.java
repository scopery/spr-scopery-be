package com.company.scopery.modules.traceability.appcomponent.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.appcomponent.application.action.CreateRegistryAppComponentAction;
import com.company.scopery.modules.traceability.appcomponent.application.command.CreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.application.service.RegistryAppComponentQueryService;
import com.company.scopery.modules.traceability.appcomponent.http.request.CreateRegistryAppComponentRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.APP_COMPONENTS)
@Tag(name = "Traceability - Application Components")
public class RegistryAppComponentController {
    private final CreateRegistryAppComponentAction create;
    private final RegistryAppComponentQueryService query;
    public RegistryAppComponentController(CreateRegistryAppComponentAction create, RegistryAppComponentQueryService query) {
        this.create=create; this.query=query;
    }
    @PostMapping @Operation(summary = "Create application component")
    public ApiResponse<RegistryAppComponentResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                             @Valid @RequestBody CreateRegistryAppComponentRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryAppComponentCommand(applicationId, workspaceId, r.code(), r.name(), r.description(), r.componentType())));
    }
    @GetMapping @Operation(summary = "List application components")
    public ApiResponse<List<RegistryAppComponentResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(applicationId));
    }
    @GetMapping("/{appComponentId}") @Operation(summary = "Get application component")
    public ApiResponse<RegistryAppComponentResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                          @PathVariable UUID appComponentId) {
        return ApiResponse.success(query.get(workspaceId, appComponentId));
    }
}
