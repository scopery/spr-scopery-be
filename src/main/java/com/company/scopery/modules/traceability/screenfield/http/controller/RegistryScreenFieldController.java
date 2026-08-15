package com.company.scopery.modules.traceability.screenfield.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screenfield.application.action.BulkCreateRegistryScreenFieldJobHandler;
import com.company.scopery.modules.traceability.screenfield.application.action.CreateRegistryScreenFieldAction;
import com.company.scopery.modules.traceability.screenfield.application.action.DeleteRegistryScreenFieldAction;
import com.company.scopery.modules.traceability.screenfield.application.action.UpdateRegistryScreenFieldAction;
import com.company.scopery.modules.traceability.screenfield.application.command.BulkCreateRegistryScreenFieldCommand;
import com.company.scopery.modules.traceability.screenfield.application.command.CreateRegistryScreenFieldCommand;
import com.company.scopery.modules.traceability.screenfield.application.command.UpdateRegistryScreenFieldCommand;
import com.company.scopery.modules.traceability.screenfield.application.response.RegistryScreenFieldResponse;
import com.company.scopery.modules.traceability.screenfield.application.service.RegistryScreenFieldQueryService;
import com.company.scopery.modules.traceability.screenfield.http.request.BulkCreateRegistryScreenFieldRequest;
import com.company.scopery.modules.traceability.screenfield.http.request.CreateRegistryScreenFieldRequest;
import com.company.scopery.modules.traceability.screenfield.http.request.UpdateRegistryScreenFieldRequest;
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
@RequestMapping(TraceabilityApiPaths.SCREEN_FIELDS)
@Tag(name = "Traceability - Screen Fields")
public class RegistryScreenFieldController {

    private final CreateRegistryScreenFieldAction create;
    private final UpdateRegistryScreenFieldAction update;
    private final DeleteRegistryScreenFieldAction delete;
    private final RegistryScreenFieldQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;

    public RegistryScreenFieldController(CreateRegistryScreenFieldAction create,
                                         UpdateRegistryScreenFieldAction update,
                                         DeleteRegistryScreenFieldAction delete,
                                         RegistryScreenFieldQueryService query,
                                         BulkJobService bulkJobService,
                                         ObjectMapper objectMapper) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create screen fields (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID workspaceId,
                                                    @PathVariable UUID screenId,
                                                    @Valid @RequestBody BulkCreateRegistryScreenFieldRequest r) {
        var items = r.items().stream()
                .map(i -> new CreateRegistryScreenFieldCommand(
                        screenId, i.sectionId(), workspaceId,
                        i.fieldKey(), i.label(), i.fieldType(), i.description(),
                        Boolean.TRUE.equals(i.required()), i.displayOrder() != null ? i.displayOrder() : 0,
                        i.componentId(), i.dataEntityFieldId(), i.maxLength(), i.remark()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(
                    new BulkCreateRegistryScreenFieldCommand(screenId, workspaceId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateRegistryScreenFieldJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @PostMapping
    @Operation(summary = "Create screen field")
    public ApiResponse<RegistryScreenFieldResponse> create(@PathVariable UUID workspaceId,
                                                            @PathVariable UUID screenId,
                                                            @Valid @RequestBody CreateRegistryScreenFieldRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryScreenFieldCommand(
                screenId, r.sectionId(), workspaceId,
                r.fieldKey(), r.label(), r.fieldType(), r.description(),
                Boolean.TRUE.equals(r.required()), r.displayOrder() != null ? r.displayOrder() : 0,
                r.componentId(), r.dataEntityFieldId(), r.maxLength(), r.remark())));
    }

    @GetMapping
    @Operation(summary = "List screen fields")
    public ApiResponse<List<RegistryScreenFieldResponse>> list(@PathVariable UUID workspaceId,
                                                               @PathVariable UUID screenId) {
        return ApiResponse.success(query.list(workspaceId, screenId));
    }

    @GetMapping("/{fieldId}")
    @Operation(summary = "Get screen field")
    public ApiResponse<RegistryScreenFieldResponse> get(@PathVariable UUID workspaceId,
                                                        @PathVariable UUID fieldId) {
        return ApiResponse.success(query.get(workspaceId, fieldId));
    }

    @PutMapping("/{fieldId}")
    @Operation(summary = "Update screen field")
    public ApiResponse<RegistryScreenFieldResponse> update(@PathVariable UUID workspaceId,
                                                            @PathVariable UUID screenId,
                                                            @PathVariable UUID fieldId,
                                                            @Valid @RequestBody UpdateRegistryScreenFieldRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryScreenFieldCommand(
                workspaceId, fieldId, r.label(), r.fieldType(), r.description(),
                Boolean.TRUE.equals(r.required()), r.displayOrder() != null ? r.displayOrder() : 0,
                r.componentId(), r.dataEntityFieldId(), r.maxLength(), r.remark())));
    }

    @DeleteMapping("/{fieldId}")
    @Operation(summary = "Delete screen field")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId,
                                    @PathVariable UUID screenId,
                                    @PathVariable UUID fieldId) {
        delete.execute(workspaceId, fieldId);
        return ApiResponse.success(null);
    }
}
