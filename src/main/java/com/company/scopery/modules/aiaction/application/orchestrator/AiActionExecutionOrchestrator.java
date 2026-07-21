package com.company.scopery.modules.aiaction.application.orchestrator;

import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStepRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class AiActionExecutionOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AiActionExecutionOrchestrator.class);

    private final AiActionExecutionRepository executionRepository;
    private final AiActionPlanRepository planRepository;
    private final AiActionStepRepository stepRepository;

    public AiActionExecutionOrchestrator(AiActionExecutionRepository executionRepository,
                                         AiActionPlanRepository planRepository,
                                         AiActionStepRepository stepRepository) {
        this.executionRepository = executionRepository;
        this.planRepository = planRepository;
        this.stepRepository = stepRepository;
    }

    @Transactional
    public void run(UUID executionId) {
        AiActionExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> AiActionExceptions.executionNotFound(executionId));

        AiActionPlan plan = planRepository.findById(execution.planId())
                .orElseThrow(() -> AiActionExceptions.planNotFound(execution.planId()));

        List<AiActionStep> steps = stepRepository.findByPlanIdOrderByOrdinal(plan.id());

        if (steps.isEmpty()) {
            // No steps — mark succeeded immediately
            execution.markSucceeded();
            plan.markCompleted();
            executionRepository.save(execution);
            planRepository.save(plan);
            log.info("Execution {} completed with no steps", executionId);
            return;
        }

        // TODO: execute each step in order via AiActionToolGateway
        log.warn("Execution orchestrator shell: {} steps pending for execution {}", steps.size(), executionId);
        execution.markSucceeded();
        plan.markCompleted();
        executionRepository.save(execution);
        planRepository.save(plan);
    }
}
