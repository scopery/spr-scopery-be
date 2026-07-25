package com.company.scopery.modules.aiaction.plan.http.controller;

import com.company.scopery.common.response.ApiResponse;
import com.company.scopery.modules.aiaction.application.action.BuildAiActionPlanAction;
import com.company.scopery.modules.aiaction.application.action.CancelAiActionPlanAction;
import com.company.scopery.modules.aiaction.application.action.ConfirmAiActionPlanAction;
import com.company.scopery.modules.aiaction.application.action.ExecuteAiActionPlanAction;
import com.company.scopery.modules.aiaction.application.action.RunAiActionPlanSyncAction;
import com.company.scopery.modules.aiaction.application.command.BuildAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.command.CancelAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.command.ConfirmAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.command.ExecuteAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.response.AiActionConfirmationResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionExecutionResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionPlanResponse;
import com.company.scopery.modules.aiaction.application.service.AiActionPlanQueryService;
import com.company.scopery.modules.aiaction.shared.constant.AiActionApiPaths;
import com.company.scopery.platform.security.JwtAuthFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(AiActionApiPaths.BASE)
@Tag(name = "AI Action - Plans")
public class AiActionPlanController {

    private final BuildAiActionPlanAction buildPlanAction;
    private final AiActionPlanQueryService planQueryService;
    private final ConfirmAiActionPlanAction confirmPlanAction;
    private final CancelAiActionPlanAction cancelPlanAction;
    private final ExecuteAiActionPlanAction executeAiActionPlanAction;
    private final RunAiActionPlanSyncAction runAiActionPlanSyncAction;

    public AiActionPlanController(BuildAiActionPlanAction buildPlanAction,
                                  AiActionPlanQueryService planQueryService,
                                  ConfirmAiActionPlanAction confirmPlanAction,
                                  CancelAiActionPlanAction cancelPlanAction,
                                  ExecuteAiActionPlanAction executeAiActionPlanAction,
                                  RunAiActionPlanSyncAction runAiActionPlanSyncAction) {
        this.buildPlanAction = buildPlanAction;
        this.planQueryService = planQueryService;
        this.confirmPlanAction = confirmPlanAction;
        this.cancelPlanAction = cancelPlanAction;
        this.executeAiActionPlanAction = executeAiActionPlanAction;
        this.runAiActionPlanSyncAction = runAiActionPlanSyncAction;
    }

    @PostMapping("/requests/{requestId}/plan")
    @Operation(summary = "Build an AI action plan for a given request")
    public ResponseEntity<ApiResponse<AiActionPlanResponse>> buildPlan(
            @PathVariable UUID requestId,
            @RequestBody Map<String, Object> body) {

        String policyCode = body.containsKey("policyCode")
                ? String.valueOf(body.get("policyCode"))
                : null;
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = currentActorId();

        BuildAiActionPlanCommand command = new BuildAiActionPlanCommand(requestId, policyCode, idempotencyKey, actorId);
        AiActionPlanResponse response = buildPlanAction.execute(command);
        return ResponseEntity.status(201).body(ApiResponse.success(response));
    }

    @GetMapping("/plans/{planId}")
    @Operation(summary = "Get an AI action plan by ID")
    public ResponseEntity<ApiResponse<AiActionPlanResponse>> getPlan(@PathVariable UUID planId) {
        AiActionPlanResponse response = planQueryService.getPlan(planId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/plans/{planId}/confirm")
    @Operation(summary = "Confirm or reject an AI action plan")
    public ResponseEntity<ApiResponse<AiActionConfirmationResponse>> confirmPlan(
            @PathVariable UUID planId,
            @RequestBody Map<String, Object> body) {

        String planHash = String.valueOf(body.getOrDefault("planHash", ""));
        int expectedPlanVersion = body.containsKey("expectedPlanVersion")
                ? Integer.parseInt(String.valueOf(body.get("expectedPlanVersion")))
                : 0;
        String decision = String.valueOf(body.getOrDefault("decision", "CONFIRM"));
        String channel = body.containsKey("channel") ? String.valueOf(body.get("channel")) : null;
        String comment = body.containsKey("comment") ? String.valueOf(body.get("comment")) : null;
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = currentActorId();

        ConfirmAiActionPlanCommand command = new ConfirmAiActionPlanCommand(
                planId, planHash, expectedPlanVersion, decision, channel, comment, idempotencyKey, actorId);
        AiActionConfirmationResponse response = confirmPlanAction.execute(command);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/plans/{planId}/cancel")
    @Operation(summary = "Cancel an AI action plan")
    public ResponseEntity<Void> cancelPlan(
            @PathVariable UUID planId,
            @RequestBody Map<String, Object> body) {

        int expectedPlanVersion = body.containsKey("expectedPlanVersion")
                ? Integer.parseInt(String.valueOf(body.get("expectedPlanVersion")))
                : 0;
        String reasonCode = body.containsKey("reasonCode") ? String.valueOf(body.get("reasonCode")) : null;
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = currentActorId();

        CancelAiActionPlanCommand command = new CancelAiActionPlanCommand(
                planId, expectedPlanVersion, reasonCode, idempotencyKey, actorId);
        cancelPlanAction.execute(command);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/plans/{planId}/execute")
    @Operation(summary = "Queue a confirmed AI action plan for execution")
    public ResponseEntity<ApiResponse<AiActionExecutionResponse>> executePlan(
            @PathVariable UUID planId,
            @RequestBody Map<String, Object> body) {

        String planHash = String.valueOf(body.getOrDefault("planHash", ""));
        int expectedPlanVersion = body.containsKey("expectedPlanVersion")
                ? Integer.parseInt(String.valueOf(body.get("expectedPlanVersion")))
                : 0;
        UUID confirmationId = body.containsKey("confirmationId") && body.get("confirmationId") != null
                ? UUID.fromString(String.valueOf(body.get("confirmationId")))
                : null;
        String idempotencyKey = body.containsKey("idempotencyKey")
                ? String.valueOf(body.get("idempotencyKey"))
                : UUID.randomUUID().toString();
        UUID actorId = currentActorId();

        ExecuteAiActionPlanCommand command = new ExecuteAiActionPlanCommand(
                planId, planHash, expectedPlanVersion, confirmationId, idempotencyKey, actorId);
        AiActionExecutionResponse response = executeAiActionPlanAction.execute(command);
        runAiActionPlanSyncAction.runAndThrowIfFailed(response.executionId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private UUID currentActorId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtAuthFilter.MapTokenDetails details) {
            return details.subjectId();
        }
        return null;
    }
}
