package com.company.scopery.modules.aiagent.playground.api;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.playground.api.request.PreviewPromptRequest;
import com.company.scopery.modules.aiagent.playground.api.request.RunPlaygroundDirectRequest;
import com.company.scopery.modules.aiagent.playground.api.request.RunPlaygroundEventConfigRequest;
import com.company.scopery.modules.aiagent.playground.application.PlaygroundApplicationService;
import com.company.scopery.modules.aiagent.playground.application.command.PreviewPromptCommand;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundDirectCommand;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundEventConfigCommand;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundOptionResponse;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundPromptPreviewResponse;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundRunResponse;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AI Agent - Playground")
@RestController
@RequestMapping(AiAgentApiPaths.PLAYGROUND)
public class PlaygroundController {

    private final PlaygroundApplicationService playgroundApplicationService;

    public PlaygroundController(PlaygroundApplicationService playgroundApplicationService) {
        this.playgroundApplicationService = playgroundApplicationService;
    }

    @Operation(summary = "Run playground by EventConfig")
    @PostMapping("/event-config/{eventConfigId}/run")
    public ResponseEntity<ApiResponse<PlaygroundRunResponse>> runEventConfig(
            @PathVariable UUID eventConfigId,
            @Valid @RequestBody RunPlaygroundEventConfigRequest request) {

        RunPlaygroundEventConfigCommand command = new RunPlaygroundEventConfigCommand(
                request.requestId(), eventConfigId, request.inputVariables());

        PlaygroundRunResponse response = playgroundApplicationService.runEventConfig(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Run playground direct (no EventConfig)")
    @PostMapping("/direct/run")
    public ResponseEntity<ApiResponse<PlaygroundRunResponse>> runDirect(
            @Valid @RequestBody RunPlaygroundDirectRequest request) {

        RunPlaygroundDirectCommand command = new RunPlaygroundDirectCommand(
                request.requestId(), request.agentId(), request.promptVersionId(),
                request.modelDeploymentId(), request.inputVariables());

        PlaygroundRunResponse response = playgroundApplicationService.runDirect(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Preview rendered prompt without running AI call")
    @PostMapping("/prompt/preview")
    public ResponseEntity<ApiResponse<PlaygroundPromptPreviewResponse>> previewPrompt(
            @Valid @RequestBody PreviewPromptRequest request) {

        PreviewPromptCommand command = new PreviewPromptCommand(
                request.promptVersionId(), request.inputVariables());

        PlaygroundPromptPreviewResponse response = playgroundApplicationService.previewPrompt(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get playground options for dropdowns")
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<PlaygroundOptionResponse>> getOptions(
            @RequestParam(defaultValue = "true") boolean includeEventConfigs,
            @RequestParam(defaultValue = "true") boolean includeAgents,
            @RequestParam(defaultValue = "true") boolean includePromptVersions,
            @RequestParam(defaultValue = "true") boolean includeModelDeployments) {

        PlaygroundOptionResponse response = playgroundApplicationService.getOptions(
                includeEventConfigs, includeAgents, includePromptVersions, includeModelDeployments);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
