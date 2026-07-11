package com.company.scopery.modules.aiagent.playground.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiagent.playground.application.action.RunEventConfigAction;
import com.company.scopery.modules.aiagent.playground.application.action.RunPlaygroundDirectAction;
import com.company.scopery.modules.aiagent.playground.application.command.PreviewPromptCommand;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundDirectCommand;
import com.company.scopery.modules.aiagent.playground.application.command.RunPlaygroundEventConfigCommand;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundOptionResponse;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundPromptPreviewResponse;
import com.company.scopery.modules.aiagent.playground.application.response.PlaygroundRunResponse;
import com.company.scopery.modules.aiagent.playground.application.service.PlaygroundQueryService;
import com.company.scopery.modules.aiagent.playground.http.request.PreviewPromptRequest;
import com.company.scopery.modules.aiagent.playground.http.request.RunPlaygroundDirectRequest;
import com.company.scopery.modules.aiagent.playground.http.request.RunPlaygroundEventConfigRequest;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "AI Agent - Playground")
@RestController
@RequestMapping(AiAgentApiPaths.PLAYGROUND)
public class PlaygroundController {

    private final RunEventConfigAction runEventConfigAction;
    private final RunPlaygroundDirectAction runPlaygroundDirectAction;
    private final PlaygroundQueryService queryService;

    public PlaygroundController(RunEventConfigAction runEventConfigAction,
                                 RunPlaygroundDirectAction runPlaygroundDirectAction,
                                 PlaygroundQueryService queryService) {
        this.runEventConfigAction = runEventConfigAction;
        this.runPlaygroundDirectAction = runPlaygroundDirectAction;
        this.queryService = queryService;
    }

    @Operation(summary = "Run playground by EventConfig")
    @PostMapping("/event-config/{eventConfigId}/run")
    public ResponseEntity<ApiResponse<PlaygroundRunResponse>> runEventConfig(
            @PathVariable UUID eventConfigId,
            @Valid @RequestBody RunPlaygroundEventConfigRequest request) {
        RunPlaygroundEventConfigCommand command = new RunPlaygroundEventConfigCommand(
                request.requestId(), eventConfigId, request.inputVariables());
        return ResponseEntity.ok(ApiResponse.success(runEventConfigAction.execute(command)));
    }

    @Operation(summary = "Run playground direct (no EventConfig)")
    @PostMapping("/direct/run")
    public ResponseEntity<ApiResponse<PlaygroundRunResponse>> runDirect(
            @Valid @RequestBody RunPlaygroundDirectRequest request) {
        RunPlaygroundDirectCommand command = new RunPlaygroundDirectCommand(
                request.requestId(), request.agentId(), request.promptVersionId(),
                request.modelDeploymentId(), request.inputVariables());
        return ResponseEntity.ok(ApiResponse.success(runPlaygroundDirectAction.execute(command)));
    }

    @Operation(summary = "Preview rendered prompt without running AI call")
    @PostMapping("/prompt/preview")
    public ResponseEntity<ApiResponse<PlaygroundPromptPreviewResponse>> previewPrompt(
            @Valid @RequestBody PreviewPromptRequest request) {
        PreviewPromptCommand command = new PreviewPromptCommand(
                request.promptVersionId(), request.inputVariables());
        return ResponseEntity.ok(ApiResponse.success(queryService.previewPrompt(command)));
    }

    @Operation(summary = "Get playground options for dropdowns")
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<PlaygroundOptionResponse>> getOptions(
            @RequestParam(defaultValue = "true") boolean includeEventConfigs,
            @RequestParam(defaultValue = "true") boolean includeAgents,
            @RequestParam(defaultValue = "true") boolean includePromptVersions,
            @RequestParam(defaultValue = "true") boolean includeModelDeployments) {
        return ResponseEntity.ok(ApiResponse.success(
                queryService.getOptions(includeEventConfigs, includeAgents,
                        includePromptVersions, includeModelDeployments)));
    }
}
