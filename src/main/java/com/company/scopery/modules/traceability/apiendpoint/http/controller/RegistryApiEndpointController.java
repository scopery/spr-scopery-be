package com.company.scopery.modules.traceability.apiendpoint.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.apiendpoint.application.action.CreateRegistryApiEndpointAction;
import com.company.scopery.modules.traceability.apiendpoint.application.action.DeleteRegistryApiEndpointAction;
import com.company.scopery.modules.traceability.apiendpoint.application.action.UpdateRegistryApiEndpointAction;
import com.company.scopery.modules.traceability.apiendpoint.application.command.CreateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.command.UpdateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.application.service.RegistryApiEndpointQueryService;
import com.company.scopery.modules.traceability.apiendpoint.http.request.CreateRegistryApiEndpointRequest;
import com.company.scopery.modules.traceability.apiendpoint.http.request.UpdateRegistryApiEndpointRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;
@RestController
@RequestMapping(TraceabilityApiPaths.API_ENDPOINTS)
@Tag(name = "Traceability - API Endpoints")
public class RegistryApiEndpointController {
    private final CreateRegistryApiEndpointAction create;
    private final UpdateRegistryApiEndpointAction update;
    private final DeleteRegistryApiEndpointAction delete;
    private final RegistryApiEndpointQueryService query;
    public RegistryApiEndpointController(CreateRegistryApiEndpointAction create, UpdateRegistryApiEndpointAction update,
                                         DeleteRegistryApiEndpointAction delete, RegistryApiEndpointQueryService query) {
        this.create=create; this.update=update; this.delete=delete; this.query=query;
    }
    @PostMapping @Operation(summary = "Create API endpoint")
    public ApiResponse<RegistryApiEndpointResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @Valid @RequestBody CreateRegistryApiEndpointRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryApiEndpointCommand(workspaceId, applicationId, r.projectId(), r.method(), r.pathPattern(), r.name())));
    }
    @GetMapping @Operation(summary = "List API endpoints")
    public ApiResponse<List<RegistryApiEndpointResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(workspaceId, applicationId));
    }
    @GetMapping("/{endpointId}") @Operation(summary = "Get API endpoint")
    public ApiResponse<RegistryApiEndpointResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID endpointId) {
        return ApiResponse.success(query.get(workspaceId, applicationId, endpointId));
    }
    @PutMapping("/{endpointId}") @Operation(summary = "Update API endpoint")
    public ApiResponse<RegistryApiEndpointResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                            @PathVariable UUID endpointId, @Valid @RequestBody UpdateRegistryApiEndpointRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryApiEndpointCommand(workspaceId, applicationId, endpointId, r.method(), r.pathPattern(), r.name())));
    }
    @DeleteMapping("/{endpointId}") @Operation(summary = "Delete API endpoint")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID endpointId) {
        delete.execute(workspaceId, applicationId, endpointId);
        return ApiResponse.success(null);
    }
}
