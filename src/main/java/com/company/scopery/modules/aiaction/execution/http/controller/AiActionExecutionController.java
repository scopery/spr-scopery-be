package com.company.scopery.modules.aiaction.execution.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiaction.application.action.CancelAiActionExecutionAction;
import com.company.scopery.modules.aiaction.application.action.CompensateAiActionExecutionAction;
import com.company.scopery.modules.aiaction.application.action.PauseAiActionExecutionAction;
import com.company.scopery.modules.aiaction.application.action.ResumeAiActionExecutionAction;
import com.company.scopery.modules.aiaction.application.command.CancelAiActionExecutionCommand;
import com.company.scopery.modules.aiaction.application.command.CompensateAiActionExecutionCommand;
import com.company.scopery.modules.aiaction.application.command.PauseAiActionExecutionCommand;
import com.company.scopery.modules.aiaction.application.command.ResumeAiActionExecutionCommand;
import com.company.scopery.modules.aiaction.application.response.AiActionControlCommandResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionExecutionEventResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionExecutionResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionStepExecutionResponse;
import com.company.scopery.modules.aiaction.application.service.AiActionExecutionEventQueryService;
import com.company.scopery.modules.aiaction.application.service.AiActionExecutionQueryService;
import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionCompensationMode;
import com.company.scopery.modules.aiaction.shared.constant.AiActionApiPaths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(AiActionApiPaths.EXECUTIONS)
@Tag(name = "AI Action - Executions")
public class AiActionExecutionController {

    private final AiActionExecutionQueryService executionQueryService;
    private final AiActionExecutionEventQueryService eventQueryService;
    private final PauseAiActionExecutionAction pauseAction;
    private final ResumeAiActionExecutionAction resumeAction;
    private final CancelAiActionExecutionAction cancelAction;
    private final CompensateAiActionExecutionAction compensateAction;

    public AiActionExecutionController(AiActionExecutionQueryService executionQueryService,
                                       AiActionExecutionEventQueryService eventQueryService,
                                       PauseAiActionExecutionAction pauseAction,
                                       ResumeAiActionExecutionAction resumeAction,
                                       CancelAiActionExecutionAction cancelAction,
                                       CompensateAiActionExecutionAction compensateAction) {
        this.executionQueryService = executionQueryService;
        this.eventQueryService = eventQueryService;
        this.pauseAction = pauseAction;
        this.resumeAction = resumeAction;
        this.cancelAction = cancelAction;
        this.compensateAction = compensateAction;
    }

    @GetMapping("/{executionId}")
    @Operation(summary = "Get an AI action execution by ID")
    public ResponseEntity<ApiResponse<AiActionExecutionResponse>> getExecution(
            @PathVariable UUID executionId) {

        AiActionExecutionResponse response = executionQueryService.getExecution(executionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{executionId}/steps")
    @Operation(summary = "List step executions for a given execution")
    public ResponseEntity<ApiResponse<List<AiActionStepExecutionResponse>>> listSteps(
            @PathVariable UUID executionId) {

        List<AiActionStepExecutionResponse> response = executionQueryService.listStepExecutions(executionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{executionId}/events")
    @Operation(summary = "Get execution events for a given execution, with optional polling cursor")
    public ResponseEntity<ApiResponse<List<AiActionExecutionEventResponse>>> getEvents(
            @PathVariable UUID executionId,
            @RequestParam(defaultValue = "0") long afterSequence,
            @RequestParam(defaultValue = "50") int limit) {

        List<AiActionExecutionEventResponse> response = eventQueryService.getEvents(executionId, afterSequence, limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{executionId}/pause")
    @Operation(summary = "Request a pause of an active execution")
    public ResponseEntity<ApiResponse<AiActionControlCommandResponse>> pause(
            @PathVariable UUID executionId,
            @RequestBody Map<String, Object> body) {

        int expectedVersion = body.containsKey("expectedVersion")
                ? Integer.parseInt(String.valueOf(body.get("expectedVersion")))
                : 0;
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = body.containsKey("actorId")
                ? UUID.fromString(String.valueOf(body.get("actorId")))
                : null;
        String reasonCode = body.containsKey("reasonCode")
                ? String.valueOf(body.get("reasonCode"))
                : null;

        PauseAiActionExecutionCommand command = new PauseAiActionExecutionCommand(
                executionId, expectedVersion, idempotencyKey, actorId, reasonCode);
        AiActionControlCommandResponse response = pauseAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{executionId}/resume")
    @Operation(summary = "Request a resume of a paused execution")
    public ResponseEntity<ApiResponse<AiActionControlCommandResponse>> resume(
            @PathVariable UUID executionId,
            @RequestBody Map<String, Object> body) {

        int expectedVersion = body.containsKey("expectedVersion")
                ? Integer.parseInt(String.valueOf(body.get("expectedVersion")))
                : 0;
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = body.containsKey("actorId")
                ? UUID.fromString(String.valueOf(body.get("actorId")))
                : null;

        ResumeAiActionExecutionCommand command = new ResumeAiActionExecutionCommand(
                executionId, expectedVersion, idempotencyKey, actorId);
        AiActionControlCommandResponse response = resumeAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{executionId}/cancel")
    @Operation(summary = "Request a cancellation of an active execution")
    public ResponseEntity<ApiResponse<AiActionControlCommandResponse>> cancel(
            @PathVariable UUID executionId,
            @RequestBody Map<String, Object> body) {

        int expectedVersion = body.containsKey("expectedVersion")
                ? Integer.parseInt(String.valueOf(body.get("expectedVersion")))
                : 0;
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = body.containsKey("actorId")
                ? UUID.fromString(String.valueOf(body.get("actorId")))
                : null;
        String reasonCode = body.containsKey("reasonCode")
                ? String.valueOf(body.get("reasonCode"))
                : null;

        CancelAiActionExecutionCommand command = new CancelAiActionExecutionCommand(
                executionId, expectedVersion, idempotencyKey, actorId, reasonCode);
        AiActionControlCommandResponse response = cancelAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{executionId}/compensate")
    @Operation(summary = "Request compensation of a completed or failed execution")
    public ResponseEntity<ApiResponse<AiActionControlCommandResponse>> compensate(
            @PathVariable UUID executionId,
            @RequestBody Map<String, Object> body) {

        int expectedVersion = body.containsKey("expectedVersion")
                ? Integer.parseInt(String.valueOf(body.get("expectedVersion")))
                : 0;
        AiActionCompensationMode mode = body.containsKey("mode")
                ? AiActionCompensationMode.valueOf(String.valueOf(body.get("mode")))
                : AiActionCompensationMode.ALL_SUPPORTED_SUCCEEDED_STEPS;
        List<UUID> targetStepIds = body.containsKey("targetStepIds")
                ? ((List<?>) body.get("targetStepIds")).stream()
                        .map(s -> UUID.fromString(String.valueOf(s)))
                        .toList()
                : List.of();
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = body.containsKey("actorId")
                ? UUID.fromString(String.valueOf(body.get("actorId")))
                : null;
        String comment = body.containsKey("comment") ? String.valueOf(body.get("comment")) : null;

        CompensateAiActionExecutionCommand command = new CompensateAiActionExecutionCommand(
                executionId, expectedVersion, mode, targetStepIds, idempotencyKey, actorId, comment);
        AiActionControlCommandResponse response = compensateAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
