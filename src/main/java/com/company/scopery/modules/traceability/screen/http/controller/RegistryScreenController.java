package com.company.scopery.modules.traceability.screen.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screen.application.action.BulkCreateRegistryScreenJobHandler;
import com.company.scopery.modules.traceability.screen.application.action.CreateRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.action.DeleteRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.action.UpdateRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.command.BulkCreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.command.CreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.command.UpdateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.application.service.RegistryScreenQueryService;
import com.company.scopery.modules.traceability.screen.http.request.BulkCreateRegistryScreenRequest;
import com.company.scopery.modules.traceability.screen.http.request.CreateRegistryScreenRequest;
import com.company.scopery.modules.traceability.screen.http.request.UpdateRegistryScreenRequest;
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
@RequestMapping(TraceabilityApiPaths.SCREENS)
@Tag(name = "Traceability - Screens")
public class RegistryScreenController {
    private final CreateRegistryScreenAction create;
    private final UpdateRegistryScreenAction update;
    private final DeleteRegistryScreenAction delete;
    private final RegistryScreenQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public RegistryScreenController(CreateRegistryScreenAction create,
                                    UpdateRegistryScreenAction update,
                                    DeleteRegistryScreenAction delete,
                                    RegistryScreenQueryService query,
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
    @Operation(summary = "Create screen")
    public ApiResponse<RegistryScreenResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                       @Valid @RequestBody CreateRegistryScreenRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenCommand(workspaceId, applicationId, r.projectId(), r.code(), r.name(), r.routePath())));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create screens (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @Valid @RequestBody BulkCreateRegistryScreenRequest r) {
        var items = r.items().stream()
                .map(i -> new CreateRegistryScreenCommand(workspaceId, applicationId, i.projectId(), i.code(), i.name(), i.routePath()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateRegistryScreenCommand(workspaceId, applicationId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateRegistryScreenJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @GetMapping
    @Operation(summary = "List screens")
    public ApiResponse<List<RegistryScreenResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(workspaceId, applicationId));
    }

    @GetMapping("/{screenId}")
    @Operation(summary = "Get screen")
    public ApiResponse<RegistryScreenResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @PathVariable UUID screenId) {
        return ApiResponse.success(query.get(workspaceId, applicationId, screenId));
    }

    @PutMapping("/{screenId}")
    @Operation(summary = "Update screen")
    public ApiResponse<RegistryScreenResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                       @PathVariable UUID screenId, @Valid @RequestBody UpdateRegistryScreenRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryScreenCommand(workspaceId, applicationId, screenId, r.name(), r.routePath())));
    }

    @DeleteMapping("/{screenId}")
    @Operation(summary = "Delete screen")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID screenId) {
        delete.execute(workspaceId, applicationId, screenId);
        return ApiResponse.success(null);
    }
}
