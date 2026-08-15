package com.company.scopery.modules.traceability.apiendpoint.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.apiendpoint.application.action.BulkCreateRegistryApiEndpointJobHandler;
import com.company.scopery.modules.traceability.apiendpoint.application.action.CreateRegistryApiEndpointAction;
import com.company.scopery.modules.traceability.apiendpoint.application.action.DeleteRegistryApiEndpointAction;
import com.company.scopery.modules.traceability.apiendpoint.application.action.UpdateRegistryApiEndpointAction;
import com.company.scopery.modules.traceability.apiendpoint.application.command.BulkCreateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.command.CreateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.command.UpdateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.application.service.RegistryApiEndpointQueryService;
import com.company.scopery.modules.traceability.apiendpoint.http.request.BulkCreateRegistryApiEndpointRequest;
import com.company.scopery.modules.traceability.apiendpoint.http.request.CreateRegistryApiEndpointRequest;
import com.company.scopery.modules.traceability.apiendpoint.http.request.UpdateRegistryApiEndpointRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import com.company.scopery.platform.bulkjob.BulkJobResponse;
import com.company.scopery.platform.bulkjob.BulkJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.API_ENDPOINTS)
@Tag(name = "Traceability - API Endpoints")
public class RegistryApiEndpointController {

    private final CreateRegistryApiEndpointAction create;
    private final UpdateRegistryApiEndpointAction update;
    private final DeleteRegistryApiEndpointAction delete;
    private final RegistryApiEndpointQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public RegistryApiEndpointController(CreateRegistryApiEndpointAction create,
                                          UpdateRegistryApiEndpointAction update,
                                          DeleteRegistryApiEndpointAction delete,
                                          RegistryApiEndpointQueryService query,
                                          BulkJobService bulkJobService,
                                          ObjectMapper objectMapper) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @Operation(summary = "Create API endpoint")
    public ApiResponse<RegistryApiEndpointResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                            @Valid @RequestBody CreateRegistryApiEndpointRequest r) {
        String paramsJson = serializeParams(r.requestParams());
        return ApiResponse.success(create.execute(new CreateRegistryApiEndpointCommand(
                workspaceId, applicationId, r.projectId(), r.method(), r.pathPattern(), r.name(),
                r.description(), paramsJson, r.responseSchemaJson())));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create API endpoints (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @Valid @RequestBody BulkCreateRegistryApiEndpointRequest r) {
        var items = r.items().stream()
                .map(i -> new CreateRegistryApiEndpointCommand(workspaceId, applicationId, i.projectId(), i.method(), i.pathPattern(), i.name(),
                        i.description(), serializeParams(i.requestParams()), i.responseSchemaJson()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateRegistryApiEndpointCommand(workspaceId, applicationId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateRegistryApiEndpointJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @GetMapping
    @Operation(summary = "List API endpoints")
    public ApiResponse<List<RegistryApiEndpointResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(workspaceId, applicationId));
    }

    @GetMapping("/{endpointId}")
    @Operation(summary = "Get API endpoint")
    public ApiResponse<RegistryApiEndpointResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                         @PathVariable UUID endpointId) {
        return ApiResponse.success(query.get(workspaceId, applicationId, endpointId));
    }

    @PutMapping("/{endpointId}")
    @Operation(summary = "Update API endpoint")
    public ApiResponse<RegistryApiEndpointResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                            @PathVariable UUID endpointId, @Valid @RequestBody UpdateRegistryApiEndpointRequest r) {
        String paramsJson = serializeParams(r.requestParams());
        return ApiResponse.success(update.execute(new UpdateRegistryApiEndpointCommand(
                workspaceId, applicationId, endpointId, r.method(), r.pathPattern(), r.name(),
                r.description(), paramsJson, r.responseSchemaJson())));
    }

    private String serializeParams(java.util.List<com.company.scopery.modules.traceability.apiendpoint.http.request.ApiParamItemRequest> params) {
        if (params == null || params.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(params);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize request params", e);
        }
    }

    @DeleteMapping("/{endpointId}")
    @Operation(summary = "Delete API endpoint")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID endpointId) {
        delete.execute(workspaceId, applicationId, endpointId);
        return ApiResponse.success(null);
    }
}
