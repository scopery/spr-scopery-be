package com.company.scopery.modules.traceability.apiendpoint.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.apiendpoint.application.action.CreateRegistryApiEndpointAction;
import com.company.scopery.modules.traceability.apiendpoint.application.command.CreateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.application.service.RegistryApiEndpointQueryService;
import com.company.scopery.modules.traceability.apiendpoint.http.request.CreateRegistryApiEndpointRequest;
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
    private final RegistryApiEndpointQueryService query;
    public RegistryApiEndpointController(CreateRegistryApiEndpointAction create, RegistryApiEndpointQueryService query) {
        this.create=create; this.query=query;
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
}
