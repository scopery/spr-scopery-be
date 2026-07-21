package com.company.scopery.modules.aiaction.application.listeners;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionExecutionMode;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionRiskLevel;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionInvocationScope;
import com.company.scopery.modules.aiaction.tool.domain.enums.AiActionToolPolicyStatus;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicy;
import com.company.scopery.modules.aiaction.tool.domain.model.AiActionToolPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// Seeds MVP tool policies on startup (idempotent — only creates records that do not already exist)
@Component
public class AiActionRegistryInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(AiActionRegistryInitializer.class);

    private final AiActionToolPolicyRepository toolPolicyRepository;

    public AiActionRegistryInitializer(AiActionToolPolicyRepository toolPolicyRepository) {
        this.toolPolicyRepository = toolPolicyRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        seedToolPolicies();
    }

    private void seedToolPolicies() {
        int seeded = 0;
        int alreadyExisted = 0;

        // --- LLM-callable policies (start ACTIVE) ---

        if (seedPolicy("agent.action.prepare", "1",
                AiActionInvocationScope.LLM_CALLABLE,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                10, false, false, false,
                AiActionToolPolicyStatus.ACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        if (seedPolicy("agent.action.status", "1",
                AiActionInvocationScope.LLM_CALLABLE_READ_ONLY,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.PREVIEW_ONLY,
                1, false, false, false,
                AiActionToolPolicyStatus.ACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        // --- Plan-execution-only policies (start INACTIVE until domain adapters are registered) ---

        if (seedPolicy("task.assign", "1",
                AiActionInvocationScope.PLAN_EXECUTION_ONLY,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                25, false, true, false,
                AiActionToolPolicyStatus.INACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        if (seedPolicy("task.estimate.update", "1",
                AiActionInvocationScope.PLAN_EXECUTION_ONLY,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                25, false, true, false,
                AiActionToolPolicyStatus.INACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        if (seedPolicy("task.mitigation.update", "1",
                AiActionInvocationScope.PLAN_EXECUTION_ONLY,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                25, false, true, false,
                AiActionToolPolicyStatus.INACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        if (seedPolicy("task.create", "1",
                AiActionInvocationScope.PLAN_EXECUTION_ONLY,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                25, false, true, false,
                AiActionToolPolicyStatus.INACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        if (seedPolicy("meeting.action.assign", "1",
                AiActionInvocationScope.PLAN_EXECUTION_ONLY,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                25, false, true, false,
                AiActionToolPolicyStatus.INACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        if (seedPolicy("meeting.action.due-date.update", "1",
                AiActionInvocationScope.PLAN_EXECUTION_ONLY,
                AiActionRiskLevel.LOW,
                AiActionExecutionMode.CONFIRM_BEFORE_EXECUTE,
                25, false, true, false,
                AiActionToolPolicyStatus.INACTIVE)) {
            seeded++;
        } else {
            alreadyExisted++;
        }

        log.info("AiActionRegistryInitializer: {} tool policies seeded, {} already existed",
                seeded, alreadyExisted);
    }

    /**
     * Seeds a single tool policy if it does not already exist.
     *
     * @return true if a new record was created, false if it already existed
     */
    private boolean seedPolicy(String toolCode, String toolVersion,
                                AiActionInvocationScope scope,
                                AiActionRiskLevel riskLevel,
                                AiActionExecutionMode executionMode,
                                int maxBatchTargets,
                                boolean dryRunRequired,
                                boolean supportsCompensation,
                                boolean supportsPause,
                                AiActionToolPolicyStatus desiredStatus) {

        if (toolPolicyRepository.findByToolCodeAndToolVersion(toolCode, toolVersion).isPresent()) {
            log.debug("AiActionRegistryInitializer: tool policy already exists — {} v{}", toolCode, toolVersion);
            return false;
        }

        AiActionToolPolicy policy = AiActionToolPolicy.create(
                toolCode, toolVersion, scope, riskLevel, executionMode,
                maxBatchTargets, dryRunRequired, supportsCompensation, supportsPause);

        if (desiredStatus == AiActionToolPolicyStatus.ACTIVE) {
            policy.activate();
        }

        toolPolicyRepository.save(policy);
        log.debug("AiActionRegistryInitializer: seeded tool policy {} v{} [{}]",
                toolCode, toolVersion, desiredStatus);
        return true;
    }
}
