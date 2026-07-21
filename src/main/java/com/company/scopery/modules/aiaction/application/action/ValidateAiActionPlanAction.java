package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.port.AiActionDryRunResult;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionPlanStatus;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStepRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ValidateAiActionPlanAction {

    private final AiActionPlanRepository planRepository;
    private final AiActionStepRepository stepRepository;
    private final AiActionToolRegistryPort toolRegistryPort;

    public ValidateAiActionPlanAction(AiActionPlanRepository planRepository,
                                       AiActionStepRepository stepRepository,
                                       AiActionToolRegistryPort toolRegistryPort) {
        this.planRepository = planRepository;
        this.stepRepository = stepRepository;
        this.toolRegistryPort = toolRegistryPort;
    }

    @Transactional
    public boolean execute(UUID planId) {
        AiActionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> AiActionExceptions.planNotFound(planId));

        if (plan.status() != AiActionPlanStatus.VALIDATING) {
            throw AiActionExceptions.planInvalidStatus(planId, plan.status().name());
        }

        List<AiActionStep> steps = stepRepository.findByPlanIdOrderByOrdinal(planId);

        boolean allValid = true;
        StringBuilder validationErrors = new StringBuilder();

        for (AiActionStep step : steps) {
            try {
                AiActionToolAdapter adapter = toolRegistryPort.requireAdapter(
                        step.toolCode(), step.toolVersion());
                AiActionDryRunResult result = adapter.dryRun(Map.of(), step);
                if (!result.success()) {
                    allValid = false;
                    validationErrors.append("Step ").append(step.ordinal())
                            .append(": ").append(result.warnings()).append("; ");
                }
            } catch (Exception e) {
                allValid = false;
                validationErrors.append("Step ").append(step.ordinal())
                        .append(": ").append(e.getMessage()).append("; ");
            }
        }

        if (allValid) {
            // Orchestrator will call markPreviewReady() with computed values
            return true;
        } else {
            plan.markInvalid();
            planRepository.save(plan);
            throw AiActionExceptions.planValidationFailed(planId, validationErrors.toString());
        }
    }
}
