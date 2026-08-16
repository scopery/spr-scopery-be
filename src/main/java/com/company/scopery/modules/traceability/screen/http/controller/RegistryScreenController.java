package com.company.scopery.modules.traceability.screen.http.controller;
import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.screen.application.action.BulkCreateRegistryScreenJobHandler;
import com.company.scopery.modules.traceability.screen.application.action.ConfirmScreenMockupUploadAction;
import com.company.scopery.modules.traceability.screen.application.action.CreateRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.action.DeleteRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.action.ImportFullScreenSpecJobHandler;
import com.company.scopery.modules.traceability.screen.application.action.RequestScreenMockupUploadAction;
import com.company.scopery.modules.traceability.screen.application.action.UpdateRegistryScreenAction;
import com.company.scopery.modules.traceability.screen.application.command.BulkCreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.command.ConfirmScreenMockupUploadCommand;
import com.company.scopery.modules.traceability.screen.application.command.CreateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.command.ImportFullScreenSpecItemCommand;
import com.company.scopery.modules.traceability.screen.application.command.ImportFullScreenSpecJobCommand;
import com.company.scopery.modules.traceability.screen.application.command.RequestScreenMockupUploadCommand;
import com.company.scopery.modules.traceability.screen.application.command.UpdateRegistryScreenCommand;
import com.company.scopery.modules.traceability.screen.application.response.RegistryScreenResponse;
import com.company.scopery.modules.traceability.screen.application.response.ScreenMockupConfirmResponse;
import com.company.scopery.modules.traceability.screen.application.response.ScreenMockupUploadResponse;
import com.company.scopery.modules.traceability.screen.application.service.RegistryScreenQueryService;
import com.company.scopery.modules.traceability.screen.http.request.BulkCreateRegistryScreenRequest;
import com.company.scopery.modules.traceability.screen.http.request.ConfirmScreenMockupUploadRequest;
import com.company.scopery.modules.traceability.screen.http.request.CreateRegistryScreenRequest;
import com.company.scopery.modules.traceability.screen.http.request.ImportFullScreenSpecRequest;
import com.company.scopery.modules.traceability.screen.http.request.RequestScreenMockupUploadRequest;
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
    private final RequestScreenMockupUploadAction requestMockupUpload;
    private final ConfirmScreenMockupUploadAction confirmMockupUpload;

    public RegistryScreenController(CreateRegistryScreenAction create,
                                    UpdateRegistryScreenAction update,
                                    DeleteRegistryScreenAction delete,
                                    RegistryScreenQueryService query,
                                    BulkJobService bulkJobService,
                                    ObjectMapper objectMapper,
                                    RequestScreenMockupUploadAction requestMockupUpload,
                                    ConfirmScreenMockupUploadAction confirmMockupUpload) {
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.query = query;
        this.bulkJobService = bulkJobService;
        this.objectMapper = objectMapper;
        this.requestMockupUpload = requestMockupUpload;
        this.confirmMockupUpload = confirmMockupUpload;
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

    @PostMapping("/{screenId}/mockup/upload-url")
    @Operation(summary = "Request presigned URL to upload screen mockup image")
    public ApiResponse<ScreenMockupUploadResponse> requestMockupUpload(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                                        @PathVariable UUID screenId, @Valid @RequestBody RequestScreenMockupUploadRequest r) {
        return ApiResponse.success(requestMockupUpload.execute(new RequestScreenMockupUploadCommand(workspaceId, applicationId, screenId, r.contentType())));
    }

    @PostMapping("/{screenId}/mockup/confirm")
    @Operation(summary = "Confirm screen mockup upload and save object key")
    public ApiResponse<ScreenMockupConfirmResponse> confirmMockupUpload(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                                         @PathVariable UUID screenId, @Valid @RequestBody ConfirmScreenMockupUploadRequest r) {
        return ApiResponse.success(confirmMockupUpload.execute(new ConfirmScreenMockupUploadCommand(workspaceId, applicationId, screenId, r.objectKey())));
    }

    @PostMapping("/import-full")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Import screens with full spec — modes, fields, mode configs, validations, process items, event items (async — poll GET /api/bulk-jobs/{id} for status)")
    public ApiResponse<BulkJobResponse> importFull(@PathVariable UUID workspaceId, @PathVariable UUID applicationId,
                                                    @Valid @RequestBody ImportFullScreenSpecRequest r) {
        var items = r.items().stream()
                .map(i -> new ImportFullScreenSpecItemCommand(
                        workspaceId, applicationId, i.projectId(),
                        i.code(), i.name(), i.routePath(),
                        toModeItems(i.modes()),
                        toFieldItems(i.fields()),
                        toProcessItems(i.processItems()),
                        toEventItems(i.eventItems())))
                .toList();
        try {
            String payload = objectMapper.writeValueAsString(new ImportFullScreenSpecJobCommand(items));
            return ApiResponse.success(BulkJobResponse.from(
                    bulkJobService.submit(ImportFullScreenSpecJobHandler.JOB_TYPE, r.items().size(), payload)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize import-full payload", e);
        }
    }

    private static List<ImportFullScreenSpecItemCommand.ModeItem> toModeItems(
            List<ImportFullScreenSpecRequest.ScreenImportItem.ModeItem> src) {
        if (src == null) return null;
        return src.stream()
                .map(m -> new ImportFullScreenSpecItemCommand.ModeItem(m.modeCode(), m.name(), m.displayOrder()))
                .toList();
    }

    private static List<ImportFullScreenSpecItemCommand.FieldItem> toFieldItems(
            List<ImportFullScreenSpecRequest.ScreenImportItem.FieldItem> src) {
        if (src == null) return null;
        return src.stream()
                .map(f -> new ImportFullScreenSpecItemCommand.FieldItem(
                        f.fieldKey(), f.label(), f.fieldType(), f.description(),
                        f.required(), f.displayOrder(), f.maxLength(), f.remark(),
                        f.componentCode(),
                        toModeConfigItems(f.modeConfigs()),
                        toValidationItems(f.validations())))
                .toList();
    }

    private static List<ImportFullScreenSpecItemCommand.FieldItem.ModeConfigItem> toModeConfigItems(
            List<ImportFullScreenSpecRequest.ScreenImportItem.FieldItem.ModeConfigItem> src) {
        if (src == null) return null;
        return src.stream()
                .map(mc -> new ImportFullScreenSpecItemCommand.FieldItem.ModeConfigItem(
                        mc.modeCode(), mc.isVisible(), mc.isRequired(), mc.isReadonly(),
                        mc.defaultValue(), mc.displayOrder()))
                .toList();
    }

    private static List<ImportFullScreenSpecItemCommand.FieldItem.ValidationItem> toValidationItems(
            List<ImportFullScreenSpecRequest.ScreenImportItem.FieldItem.ValidationItem> src) {
        if (src == null) return null;
        return src.stream()
                .map(v -> new ImportFullScreenSpecItemCommand.FieldItem.ValidationItem(
                        v.modeCode(), v.ruleTypeCode(), v.ruleParamJson(),
                        v.conditionJson(), v.errorMessage(), v.remark(), v.displayOrder()))
                .toList();
    }

    private static List<ImportFullScreenSpecItemCommand.ProcessItem> toProcessItems(
            List<ImportFullScreenSpecRequest.ScreenImportItem.ProcessItem> src) {
        if (src == null) return null;
        return src.stream()
                .map(p -> new ImportFullScreenSpecItemCommand.ProcessItem(
                        p.modeCode(), p.targetFieldKey(), p.title(),
                        p.content(), p.sourceTable(), p.conditionNote(), p.displayOrder()))
                .toList();
    }

    private static List<ImportFullScreenSpecItemCommand.EventItem> toEventItems(
            List<ImportFullScreenSpecRequest.ScreenImportItem.EventItem> src) {
        if (src == null) return null;
        return src.stream()
                .map(e -> new ImportFullScreenSpecItemCommand.EventItem(
                        e.modeCode(), e.triggerFieldKey(), e.triggerActionCode(),
                        e.title(), e.content(), e.conditionNote(),
                        e.targetScreenCode(), e.targetModeCode(), e.displayOrder()))
                .toList();
    }
}
