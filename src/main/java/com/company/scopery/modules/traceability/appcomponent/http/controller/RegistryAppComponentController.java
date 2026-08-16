package com.company.scopery.modules.traceability.appcomponent.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.appcomponent.application.action.BulkCreateRegistryAppComponentJobHandler;
import com.company.scopery.modules.traceability.appcomponent.application.action.ConfirmComponentScreenshotUploadAction;
import com.company.scopery.modules.traceability.appcomponent.application.action.CreateRegistryAppComponentAction;
import com.company.scopery.modules.traceability.appcomponent.application.action.DeleteRegistryAppComponentAction;
import com.company.scopery.modules.traceability.appcomponent.application.action.ImportFullAppComponentJobHandler;
import com.company.scopery.modules.traceability.appcomponent.application.action.RequestComponentScreenshotUploadAction;
import com.company.scopery.modules.traceability.appcomponent.application.action.UpdateRegistryAppComponentAction;
import com.company.scopery.modules.traceability.appcomponent.application.command.BulkCreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.ConfirmComponentScreenshotUploadCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.CreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.ImportFullAppComponentItemCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.ImportFullAppComponentJobCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.RequestComponentScreenshotUploadCommand;
import com.company.scopery.modules.traceability.appcomponent.application.command.UpdateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.ComponentScreenshotConfirmResponse;
import com.company.scopery.modules.traceability.appcomponent.application.response.ComponentScreenshotUploadResponse;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.application.service.RegistryAppComponentQueryService;
import com.company.scopery.modules.traceability.appcomponent.http.request.BulkCreateRegistryAppComponentRequest;
import com.company.scopery.modules.traceability.appcomponent.http.request.ConfirmComponentScreenshotUploadRequest;
import com.company.scopery.modules.traceability.appcomponent.http.request.CreateRegistryAppComponentRequest;
import com.company.scopery.modules.traceability.appcomponent.http.request.ImportFullAppComponentRequest;
import com.company.scopery.modules.traceability.appcomponent.http.request.RequestComponentScreenshotUploadRequest;
import com.company.scopery.modules.traceability.appcomponent.http.request.UpdateRegistryAppComponentRequest;
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
@RequestMapping(TraceabilityApiPaths.APP_COMPONENTS)
@Tag(name = "Traceability - Application Components")
public class RegistryAppComponentController {

    private final CreateRegistryAppComponentAction create;
    private final UpdateRegistryAppComponentAction update;
    private final DeleteRegistryAppComponentAction delete;
    private final RegistryAppComponentQueryService query;
    private final BulkJobService bulkJobService;
    private final ObjectMapper objectMapper;
    private final RequestComponentScreenshotUploadAction requestScreenshotUpload;
    private final ConfirmComponentScreenshotUploadAction confirmScreenshotUpload;

    public RegistryAppComponentController(CreateRegistryAppComponentAction create,
                                           UpdateRegistryAppComponentAction update,
                                           DeleteRegistryAppComponentAction delete,
                                           RegistryAppComponentQueryService query,
                                           BulkJobService bulkJobService,
                                           ObjectMapper objectMapper,
                                           RequestComponentScreenshotUploadAction requestScreenshotUpload,
                                           ConfirmComponentScreenshotUploadAction confirmScreenshotUpload) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
        this.requestScreenshotUpload = requestScreenshotUpload;
        this.confirmScreenshotUpload = confirmScreenshotUpload;
    }

    @PostMapping
    @Operation(summary = "Create application component")
    public ApiResponse<RegistryAppComponentResponse> create(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                             @Valid @RequestBody CreateRegistryAppComponentRequest r) {
        return ApiResponse.success(create.execute(new CreateRegistryAppComponentCommand(applicationId, workspaceId, r.code(), r.name(), r.description(), r.componentType(), r.optionSourceType(), r.sourceEntityId(), r.sourceValueColumn(), r.sourceLabelColumn(), r.sourceFilterJson())));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Bulk create application components (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> bulkCreate(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @Valid @RequestBody BulkCreateRegistryAppComponentRequest r) {
        var items = r.items().stream()
                .map(i -> new CreateRegistryAppComponentCommand(applicationId, workspaceId, i.code(), i.name(), i.description(), i.componentType(), i.optionSourceType(), i.sourceEntityId(), i.sourceValueColumn(), i.sourceLabelColumn(), i.sourceFilterJson()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new BulkCreateRegistryAppComponentCommand(applicationId, workspaceId, items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(BulkCreateRegistryAppComponentJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize bulk create payload", e);
        }
    }

    @PostMapping("/import-full")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Import full app components with fields (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> importFull(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @Valid @RequestBody ImportFullAppComponentRequest r) {
        var items = r.items().stream()
                .map(i -> new ImportFullAppComponentItemCommand(
                        applicationId, workspaceId,
                        i.code(), i.name(), i.description(),
                        i.componentType(), i.optionSourceType(),
                        i.sourceEntityId(), i.sourceValueColumn(), i.sourceLabelColumn(), i.sourceFilterJson(),
                        i.fields() == null ? null : i.fields().stream()
                                .map(f -> new ImportFullAppComponentItemCommand.FieldItem(
                                        f.fieldKey(), f.label(), f.fieldType(),
                                        Boolean.TRUE.equals(f.required()),
                                        f.maxLength(), f.remark(),
                                        f.displayOrder() != null ? f.displayOrder() : 0))
                                .toList()))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new ImportFullAppComponentJobCommand(items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(ImportFullAppComponentJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize import-full payload", e);
        }
    }

    @GetMapping
    @Operation(summary = "List application components")
    public ApiResponse<List<RegistryAppComponentResponse>> list(@PathVariable UUID workspaceId, @PathVariable UUID applicationId) {
        return ApiResponse.success(query.list(applicationId));
    }

    @GetMapping("/{appComponentId}")
    @Operation(summary = "Get application component")
    public ApiResponse<RegistryAppComponentResponse> get(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                          @PathVariable UUID appComponentId) {
        return ApiResponse.success(query.get(workspaceId, appComponentId));
    }

    @PutMapping("/{appComponentId}")
    @Operation(summary = "Update application component")
    public ApiResponse<RegistryAppComponentResponse> update(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                             @PathVariable UUID appComponentId, @Valid @RequestBody UpdateRegistryAppComponentRequest r) {
        return ApiResponse.success(update.execute(new UpdateRegistryAppComponentCommand(workspaceId, appComponentId, r.name(), r.description(), r.componentType(), r.optionSourceType(), r.sourceEntityId(), r.sourceValueColumn(), r.sourceLabelColumn(), r.sourceFilterJson())));
    }

    @DeleteMapping("/{appComponentId}")
    @Operation(summary = "Delete application component")
    public ApiResponse<Void> delete(@PathVariable UUID workspaceId, @PathVariable UUID applicationId, @PathVariable UUID appComponentId) {
        delete.execute(workspaceId, appComponentId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{appComponentId}/screenshot/upload-url")
    @Operation(summary = "Request presigned URL to upload component screenshot image")
    public ApiResponse<ComponentScreenshotUploadResponse> requestScreenshotUpload(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                                                   @PathVariable UUID appComponentId, @Valid @RequestBody RequestComponentScreenshotUploadRequest r) {
        return ApiResponse.success(requestScreenshotUpload.execute(new RequestComponentScreenshotUploadCommand(workspaceId, applicationId, appComponentId, r.contentType())));
    }

    @PostMapping("/{appComponentId}/screenshot/confirm")
    @Operation(summary = "Confirm component screenshot upload and save object key")
    public ApiResponse<ComponentScreenshotConfirmResponse> confirmScreenshotUpload(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                                                    @PathVariable UUID appComponentId, @Valid @RequestBody ConfirmComponentScreenshotUploadRequest r) {
        return ApiResponse.success(confirmScreenshotUpload.execute(new ConfirmComponentScreenshotUploadCommand(workspaceId, applicationId, appComponentId, r.objectKey())));
    }
}
