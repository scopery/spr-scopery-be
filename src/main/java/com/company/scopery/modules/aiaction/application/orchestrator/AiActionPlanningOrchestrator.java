package com.company.scopery.modules.aiaction.application.orchestrator;

import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStepRepository;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Component
public class AiActionPlanningOrchestrator {

    private final AiActionToolRegistryPort toolRegistryPort;
    private final AiActionPlanRepository planRepository;
    private final AiActionStepRepository stepRepository;

    public AiActionPlanningOrchestrator(AiActionToolRegistryPort toolRegistryPort,
                                        AiActionPlanRepository planRepository,
                                        AiActionStepRepository stepRepository) {
        this.toolRegistryPort = toolRegistryPort;
        this.planRepository = planRepository;
        this.stepRepository = stepRepository;
    }

    public AiActionPlan plan(AiActionRequest request, String policyCode) {
        // Load the latest plan number for this request
        int planNumber = planRepository.findLatestByRequestId(request.id())
                .map(p -> p.planNumber() + 1)
                .orElse(1);

        AiActionPlan plan = AiActionPlan.create(request.id(), planNumber, policyCode, 1);

        // Mark validating during orchestration
        plan.markValidating();

        // For MVP: produce a minimal preview-ready plan with no steps
        // Real planning (LLM -> step resolution) is wired in a later iteration
        String planHash = computePlanHash(request.id().toString(), policyCode, String.valueOf(planNumber));

        plan.markPreviewReady(
                planHash,
                computePlanHash(request.id().toString(), "context", String.valueOf(planNumber)),
                computePlanHash(request.id().toString(), "state", String.valueOf(planNumber)),
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                true, 0, 0,
                "AI action plan for request: " + request.id(),
                Instant.now().plusSeconds(1800));

        return planRepository.save(plan);
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
