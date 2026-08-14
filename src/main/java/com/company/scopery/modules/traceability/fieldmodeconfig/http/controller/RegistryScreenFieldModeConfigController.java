package com.company.scopery.modules.traceability.fieldmodeconfig.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.action.DeleteScreenFieldModeConfigAction;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.action.ReplaceScreenFieldModeConfigsAction;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.command.DeleteScreenFieldModeConfigCommand;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.command.ReplaceScreenFieldModeConfigsCommand;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.command.ReplaceScreenFieldModeConfigsCommand.ModeConfigItem;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.response.RegistryScreenFieldModeConfigResponse;
import com.company.scopery.modules.traceability.fieldmodeconfig.application.service.RegistryScreenFieldModeConfigQueryService;
import com.company.scopery.modules.traceability.fieldmodeconfig.http.request.ReplaceScreenFieldModeConfigsRequest;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(TraceabilityApiPaths.SCREEN_FIELD_MODE_CONFIGS)
@Tag(name = "Traceability - Screen Field Mode Configs")
public class RegistryScreenFieldModeConfigController {

    private final ReplaceScreenFieldModeConfigsAction replace;
    private final DeleteScreenFieldModeConfigAction delete;
    private final RegistryScreenFieldModeConfigQueryService query;

    public RegistryScreenFieldModeConfigController(ReplaceScreenFieldModeConfigsAction replace,
                                                    DeleteScreenFieldModeConfigAction delete,
                                                    RegistryScreenFieldModeConfigQueryService query) {
        this.replace = replace;
        this.delete = delete;
        this.query = query;
    }

    @PutMapping
    @Operation(summary = "Replace all active mode configs for a field (atomic upsert)")
    public ApiResponse<List<RegistryScreenFieldModeConfigResponse>> replace(
            @PathVariable UUID workspaceId,
            @PathVariable UUID screenId,
            @PathVariable UUID fieldId,
            @Valid @RequestBody ReplaceScreenFieldModeConfigsRequest r) {
        List<ModeConfigItem> items = r.modeConfigs().stream()
                .map(i -> new ModeConfigItem(i.modeId(), i.isVisible(), i.isRequired(),
                        i.isReadonly(), i.defaultValue(), i.displayOrder()))
                .toList();
        return ApiResponse.success(replace.execute(
                new ReplaceScreenFieldModeConfigsCommand(workspaceId, screenId, fieldId, items)));
    }

    @GetMapping
    @Operation(summary = "List mode configs for a field")
    public ApiResponse<List<RegistryScreenFieldModeConfigResponse>> list(
            @PathVariable UUID workspaceId,
            @PathVariable UUID fieldId) {
        return ApiResponse.success(query.listByFieldId(workspaceId, fieldId));
    }

    @GetMapping("/{configId}")
    @Operation(summary = "Get a single mode config by ID")
    public ApiResponse<RegistryScreenFieldModeConfigResponse> get(
            @PathVariable UUID workspaceId,
            @PathVariable UUID configId) {
        return ApiResponse.success(query.getById(workspaceId, configId));
    }

    @DeleteMapping("/{configId}")
    @Operation(summary = "Delete a single mode config")
    public ApiResponse<Void> delete(
            @PathVariable UUID workspaceId,
            @PathVariable UUID configId) {
        delete.execute(new DeleteScreenFieldModeConfigCommand(workspaceId, configId));
        return ApiResponse.success(null);
    }
}
