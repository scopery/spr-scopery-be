package com.company.scopery.modules.traceability.dataentity.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.dataentity.application.action.BulkCreateRegistryDataEntityJobHandler;
import com.company.scopery.modules.traceability.dataentity.application.action.CreateRegistryDataEntityAction;
import com.company.scopery.modules.traceability.dataentity.application.action.DeleteRegistryDataEntityAction;
import com.company.scopery.modules.traceability.dataentity.application.action.UpdateRegistryDataEntityAction;
import com.company.scopery.modules.traceability.dataentity.application.command.BulkCreateRegistryDataEntityCommand;
import com.company.scopery.modules.traceability.dataentity.application.command.CreateRegistryDataEntityCommand;
import com.company.scopery.modules.traceability.dataentity.application.command.UpdateRegistryDataEntityCommand;
import com.company.scopery.modules.traceability.dataentity.application.response.RegistryDataEntityResponse;
import com.company.scopery.modules.traceability.dataentity.application.service.RegistryDataEntityQueryService;
import com.company.scopery.modules.traceability.dataentity.http.request.BulkCreateRegistryDataEntityRequest;
import com.company.scopery.modules.traceability.dataentity.http.request.CreateRegistryDataEntityRequest;
import com.company.scopery.modules.traceability.dataentity.http.request.UpdateRegistryDataEntityRequest;
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
@RequestMapping(TraceabilityApiPaths.DATA_ENTITIES)
@Tag(name = "Traceability - Data Entities")
public class RegistryDataEntityController {

    private final CreateRegistryDataEntityAction create;
    private final UpdateRegistryDataEntityAction update;
    private final DeleteRegistryDataEntityAction delete;
    private final RegistryDataEntityQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public RegistryDataEntityController(CreateRegistryDataEntityAction create,
                                         UpdateRegistryDataEntityAction update,
                                         DeleteRegistryDataEntityAction delete,
                                         RegistryDataEntityQueryService query,
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
    @Operation(summary = "Create data entity")
    public ApiResponse<RegistryDataEntityResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                           @Valid @RequestBody CreateRegistryDataEntityRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryDataEntityCommand(applicationId, workspaceId, r.moduleId(), r.code(), r.name(), r.description(), r.tableName())));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create data entities (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @Valid @RequestBody BulkCreateRegistryDataEntityRequest r) {
        var items = r.items().stream()
                .map(i -> new CreateRegistryDataEntityCommand(applicationId, workspaceId, i.moduleId(), i.code(), i.name(), i.description(), i.tableName()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateRegistryDataEntityCommand(applicationId, workspaceId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateRegistryDataEntityJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @GetMapping
    @Operation(summary = "List data entities")
    public ApiResponse<List<RegistryDataEntityResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                               @RequestParam(required = false) UUID moduleId) {
        return ApiResponse.success(moduleId != null
                ? query.listByModule(workspaceId, applicationId, moduleId)
                : query.list(workspaceId, applicationId));
    }

    @GetMapping("/{dataEntityId}")
    @Operation(summary = "Get data entity")
    public ApiResponse<RegistryDataEntityResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                        @PathVariable UUID dataEntityId) {
        return ApiResponse.success(query.get(workspaceId, dataEntityId));
    }

    @PutMapping("/{dataEntityId}")
    @Operation(summary = "Update data entity")
    public ApiResponse<RegistryDataEntityResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                           @PathVariable UUID dataEntityId, @Valid @RequestBody UpdateRegistryDataEntityRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryDataEntityCommand(workspaceId, dataEntityId, r.moduleId(), r.name(), r.description(), r.tableName())));
    }

    @DeleteMapping("/{dataEntityId}")
    @Operation(summary = "Delete data entity")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID dataEntityId) {
        delete.execute(workspaceId, dataEntityId);
        return ApiResponse.success(null);
    }
}
