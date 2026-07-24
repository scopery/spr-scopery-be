package com.company.scopery.modules.aiassistant.workspaceconfig.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantApiPaths;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.response.AiAssistantWorkspaceConfigResponse;
import com.company.scopery.modules.aiassistant.workspaceconfig.application.service.AiAssistantWorkspaceConfigQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AI Assistant - Workspace Config")
@RestController
@RequestMapping(AiAssistantApiPaths.WORKSPACE_CONFIG)
public class AiAssistantWorkspaceConfigController {

    private final AiAssistantWorkspaceConfigQueryService queryService;

    public AiAssistantWorkspaceConfigController(AiAssistantWorkspaceConfigQueryService queryService) {
        this.queryService = queryService;
    }

    @Operation(summary = "Get AI Assistant workspace config for the current workspace")
    @GetMapping
    public ResponseEntity<ApiResponse<AiAssistantWorkspaceConfigResponse>> getWorkspaceConfig(
            @RequestHeader(value = "X-Workspace-Id", required = false) UUID workspaceId) {

        AiAssistantWorkspaceConfigResponse response = queryService.findByWorkspaceId(workspaceId).orElse(null);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
