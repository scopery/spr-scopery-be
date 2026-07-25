package com.company.scopery.modules.traceability.appmodule.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.appmodule.application.action.BulkCreateRegistryAppModuleJobHandler;
import com.company.scopery.modules.traceability.appmodule.application.action.CreateRegistryAppModuleAction;
import com.company.scopery.modules.traceability.appmodule.application.action.DeleteRegistryAppModuleAction;
import com.company.scopery.modules.traceability.appmodule.application.action.UpdateRegistryAppModuleAction;
import com.company.scopery.modules.traceability.appmodule.application.command.BulkCreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.command.CreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.command.UpdateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.response.RegistryAppModuleResponse;
import com.company.scopery.modules.traceability.appmodule.application.service.RegistryAppModuleQueryService;
import com.company.scopery.modules.traceability.appmodule.http.request.BulkCreateRegistryAppModuleRequest;
import com.company.scopery.modules.traceability.appmodule.http.request.CreateRegistryAppModuleRequest;
import com.company.scopery.modules.traceability.appmodule.http.request.UpdateRegistryAppModuleRequest;
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
@RequestMapping(TraceabilityApiPaths.APP_MODULES)
@Tag(name = "Traceability - Application Modules")
public class RegistryAppModuleController {

    private final CreateRegistryAppModuleAction create;
    private final UpdateRegistryAppModuleAction update;
    private final DeleteRegistryAppModuleAction delete;
    private final RegistryAppModuleQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public RegistryAppModuleController(CreateRegistryAppModuleAction create,
                                       UpdateRegistryAppModuleAction update,
                                       DeleteRegistryAppModuleAction delete,
                                       RegistryAppModuleQueryService query,
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
    @Operation(summary = "Create application module")
    public ApiResponse<RegistryAppModuleResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                          @Valid @RequestBody CreateRegistryAppModuleRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryAppModuleCommand(applicationId, workspaceId, r.code(), r.name(), r.description())));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create application modules (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @Valid @RequestBody BulkCreateRegistryAppModuleRequest r) {
        var items = r.items().stream()
                .map(item -> new CreateRegistryAppModuleCommand(applicationId, workspaceId, item.code(), item.name(), item.description()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateRegistryAppModuleCommand(applicationId, workspaceId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateRegistryAppModuleJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @GetMapping
    @Operation(summary = "List application modules")
    public ApiResponse<List<RegistryAppModuleResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(applicationId));
    }

    @GetMapping("/{appModuleId}")
    @Operation(summary = "Get application module")
    public ApiResponse<RegistryAppModuleResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                       @PathVariable UUID appModuleId) {
        return ApiResponse.success(query.get(workspaceId, appModuleId));
    }

    @PutMapping("/{appModuleId}")
    @Operation(summary = "Update application module")
    public ApiResponse<RegistryAppModuleResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                          @PathVariable UUID appModuleId, @Valid @RequestBody UpdateRegistryAppModuleRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryAppModuleCommand(workspaceId, appModuleId, r.name(), r.description())));
    }

    @DeleteMapping("/{appModuleId}")
    @Operation(summary = "Delete application module")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID appModuleId) {
        delete.execute(workspaceId, appModuleId);
        return ApiResponse.success(null);
    }
}
