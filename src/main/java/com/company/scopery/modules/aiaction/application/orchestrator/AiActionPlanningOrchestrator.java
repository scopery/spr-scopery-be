package com.company.scopery.modules.aiaction.application.orchestrator;

import com.company.scopery.modules.aiaction.application.port.AiActionRequestedAction;
import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStepRepository;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Component
public class AiActionPlanningOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AiActionPlanningOrchestrator.class);

    private final AiActionToolRegistryPort toolRegistryPort;
    private final AiActionPlanRepository planRepository;
    private final AiActionStepRepository stepRepository;
    private final ObjectMapper objectMapper;

    public AiActionPlanningOrchestrator(AiActionToolRegistryPort toolRegistryPort,
                                        AiActionPlanRepository planRepository,
                                        AiActionStepRepository stepRepository,
                                        ObjectMapper objectMapper) {
        this.toolRegistryPort = toolRegistryPort;
        this.planRepository = planRepository;
        this.stepRepository = stepRepository;
        this.objectMapper = objectMapper;
    }

    public AiActionPlan plan(AiActionRequest request, String policyCode) {
        int planNumber = planRepository.findLatestByRequestId(request.id())
                .map(p -> p.planNumber() + 1)
                .orElse(1);

        AiActionPlan plan = AiActionPlan.create(request.id(), planNumber, policyCode, 1);
        plan.markValidating();

        List<AiActionRequestedAction> requestedActions = parseRequestedActions(request.requestedActionsJson());
        List<AiActionStep> steps = buildSteps(plan.id(), requestedActions);

        AiActionRiskLevel planRisk = computePlanRisk(requestedActions);
        boolean requiresConfirmation = !steps.isEmpty();

        String planHash = computePlanHash(request.id().toString(), policyCode, String.valueOf(planNumber));
        String summary = requestedActions.isEmpty()
                ? "AI action plan for request: " + request.id()
                : buildSummary(requestedActions);

        plan.markPreviewReady(
                planHash,
                computePlanHash(request.id().toString(), "context", String.valueOf(planNumber)),
                computePlanHash(request.id().toString(), "state", String.valueOf(planNumber)),
                planRisk,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                requiresConfirmation,
                steps.size(),
                (int) requestedActions.stream().filter(a -> a.targetEntityId() != null).count(),
                summary,
                Instant.now().plusSeconds(1800));

        AiActionPlan savedPlan = planRepository.save(plan);

        if (!steps.isEmpty()) {
            List<AiActionStep> stepsWithPlanId = steps.stream()
                    .map(s -> AiActionStep.create(
                            savedPlan.id(), s.ordinal(), s.toolCode(), s.toolVersion(),
                            s.inputSchemaCode(), s.inputSchemaVersion(), s.inputHash(),
                            s.targetEntityType(), s.targetEntityId(),
                            s.expectedTargetVersionToken(), s.riskLevel(),
                            s.executionMode(), s.dependsOnStepIds()))
                    .toList();
            stepRepository.saveAll(stepsWithPlanId);
        }

        return savedPlan;
    }

    private List<AiActionRequestedAction> parseRequestedActions(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("[AiActionPlanningOrchestrator] Failed to parse requestedActionsJson: {}", e.getMessage());
            return List.of();
        }
    }

    private List<AiActionStep> buildSteps(java.util.UUID planId, List<AiActionRequestedAction> actions) {
        List<AiActionStep> steps = new ArrayList<>();
        int ordinal = 1;
        for (AiActionRequestedAction action : actions) {
            AiActionRiskLevel stepRisk = resolveStepRisk(action.toolCode(), action.toolVersion());
            String inputHash = computePlanHash(
                    action.toolCode(), action.toolVersion(),
                    String.valueOf(action.inputArguments()));
            steps.add(AiActionStep.create(
                    planId, ordinal++,
                    action.toolCode(), action.toolVersion(),
                    action.toolCode() + "_input", 1,
                    inputHash,
                    action.targetEntityType(), action.targetEntityId(),
                    null,
                    stepRisk,
                    AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                    List.of()));
        }
        return steps;
    }

    private AiActionRiskLevel resolveStepRisk(String toolCode, String toolVersion) {
        return toolRegistryPort.findPolicy(toolCode, toolVersion)
                .filter(p -> p.status() == AiActionToolPolicyStatus.ACTIVE)
                .map(AiActionToolPolicy::riskLevel)
                .orElse(AiActionRiskLevel.MEDIUM);
    }

    private AiActionRiskLevel computePlanRisk(List<AiActionRequestedAction> actions) {
        AiActionRiskLevel max = AiActionRiskLevel.LOW;
        for (AiActionRequestedAction action : actions) {
            AiActionRiskLevel r = resolveStepRisk(action.toolCode(), action.toolVersion());
            if (r.ordinal() > max.ordinal()) max = r;
        }
        return max;
    }

    private String buildSummary(List<AiActionRequestedAction> actions) {
        if (actions.size() == 1) {
            AiActionRequestedAction a = actions.get(0);
            return buildSingleActionSummary(a);
        }
        return actions.size() + " actions: " + actions.stream()
                .map(AiActionRequestedAction::toolCode).distinct()
                .reduce((a, b) -> a + ", " + b).orElse("");
    }

    private String buildSingleActionSummary(AiActionRequestedAction a) {
        Map<String, Object> args = a.inputArguments() != null ? a.inputArguments() : Map.of();
        return switch (a.toolCode()) {
            case "create_task" -> {
                String title = stringify(args.get("title"));
                String priority = stringify(args.get("priority"));
                yield "Create task" + (title != null ? ": \"" + title + "\"" : "")
                        + (priority != null ? " [" + priority + "]" : "");
            }
            case "update_task_status" -> {
                String newStatus = stringify(args.get("newStatus"));
                yield "Update task status" + (newStatus != null ? " → " + newStatus : "");
            }
            default -> a.toolCode() + (a.targetEntityId() != null ? " on " + a.targetEntityId() : "");
        };
    }

    private static String stringify(Object v) {
        return v != null ? v.toString() : null;
    }

    private String computePlanHash(String... parts) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = String.join("|", parts);
            return "plan:v1:sha256:" + HexFormat.of().formatHex(
                    digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
