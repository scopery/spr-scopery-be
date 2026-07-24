package com.company.scopery.modules.aiassistant.workspaceconfig.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.action.UpsertAiAssistantWorkspaceConfigAction;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.command.UpsertAiAssistantWorkspaceConfigCommand;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.response.AiAssistantWorkspaceConfigResponse;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.service.AiAssistantWorkspaceConfigQueryService;
import com.company.scopery.modules.aiassistant.workspaceconfig.http.request.UpsertAiAssistantWorkspaceConfigRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AI Assistant - Workspace Config")
@RestController
@RequestMapping(AiAssistantApiPaths.ADMIN_WORKSPACE_CONFIG)
public class AdminAiAssistantWorkspaceConfigController {

    private final UpsertAiAssistantWorkspaceConfigAction upsertAction;
    private final AiAssistantWorkspaceConfigQueryService queryService;

    public AdminAiAssistantWorkspaceConfigController(
            UpsertAiAssistantWorkspaceConfigAction upsertAction,
            AiAssistantWorkspaceConfigQueryService queryService) {
        this.upsertAction = upsertAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Upsert AI Assistant workspace config for a specific workspace")
    @PutMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<AiAssistantWorkspaceConfigResponse>> upsertWorkspaceConfig(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody UpsertAiAssistantWorkspaceConfigRequest request) {

        UpsertAiAssistantWorkspaceConfigCommand command = new UpsertAiAssistantWorkspaceConfigCommand(
                workspaceId,
                request.modelDeploymentId(),
                request.modelProvider(),
                request.modelName(),
                request.systemPromptOverride(),
                request.temperatureOverride(),
                request.maxOutputTokensOverride()
        );
        AiAssistantWorkspaceConfigResponse response = upsertAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get AI Assistant workspace config for a specific workspace")
    @GetMapping("/{workspaceId}")
    public ResponseEntity<ApiResponse<AiAssistantWorkspaceConfigResponse>> getWorkspaceConfig(
            @PathVariable UUID workspaceId) {

        AiAssistantWorkspaceConfigResponse response = queryService.findByWorkspaceId(workspaceId).orElse(null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
