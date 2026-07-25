package com.company.scopery.modules.aiaction.application.orchestrator;

import com.company.scopery.modules.aiaction.application.port.AiActionRequestedAction;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionExecutionStatus;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecutionRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStepRepository;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequestRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AiActionExecutionOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AiActionExecutionOrchestrator.class);

    private final AiActionExecutionRepository executionRepository;
    private final AiActionPlanRepository planRepository;
    private final AiActionStepRepository stepRepository;
    private final AiActionStepExecutionRepository stepExecutionRepository;
    private final AiActionRequestRepository requestRepository;
    private final AiActionToolRegistryPort toolRegistryPort;
    private final ObjectMapper objectMapper;

    public AiActionExecutionOrchestrator(AiActionExecutionRepository executionRepository,
                                         AiActionPlanRepository planRepository,
                                         AiActionStepRepository stepRepository,
                                         AiActionStepExecutionRepository stepExecutionRepository,
                                         AiActionRequestRepository requestRepository,
                                         AiActionToolRegistryPort toolRegistryPort,
                                         ObjectMapper objectMapper) {
        this.executionRepository = executionRepository;
        this.planRepository = planRepository;
        this.stepRepository = stepRepository;
        this.stepExecutionRepository = stepExecutionRepository;
        this.requestRepository = requestRepository;
        this.toolRegistryPort = toolRegistryPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void run(UUID executionId) {
        AiActionExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> AiActionExceptions.executionNotFound(executionId));

        if (execution.status() != AiActionExecutionStatus.RUNNING) {
            log.warn("[AiActionOrchestrator] Execution {} is not RUNNING (status={}), skipping",
                    executionId, execution.status());
            return;
        }

        AiActionPlan plan = planRepository.findById(execution.planId())
                .orElseThrow(() -> AiActionExceptions.planNotFound(execution.planId()));

        List<AiActionStep> steps = stepRepository.findByPlanIdOrderByOrdinal(plan.id());

        if (steps.isEmpty()) {
            execution.markSucceeded();
            plan.markCompleted();
            executionRepository.save(execution);
            planRepository.save(plan);
            log.info("[AiActionOrchestrator] Execution {} completed with no steps", executionId);
            return;
        }

        AiActionRequest request = requestRepository.findById(plan.requestId()).orElse(null);
        List<AiActionRequestedAction> requestedActions = parseRequestedActions(
                request != null ? request.requestedActionsJson() : null);

        boolean anyFailed = false;

        for (AiActionStep step : steps) {
            Map<String, Object> inputArgs = resolveInputArgs(requestedActions, step.ordinal());

            String stepKey = "step:" + execution.id() + ":" + step.id();
            AiActionStepExecution stepExec = AiActionStepExecution.create(
                    execution.id(), step.id(), step.ordinal(), step.toolCode(), 1, stepKey);
            stepExecutionRepository.save(stepExec);

            execution.recordStepStarted(step.ordinal());
            executionRepository.save(execution);

            try {
                AiActionToolAdapter adapter = toolRegistryPort.requireAdapter(step.toolCode(), step.toolVersion());
                AiActionToolResult result = adapter.execute(inputArgs, step, execution);

                if (result.status() == AiActionToolResult.Status.SUCCEEDED) {
                    stepExec.markSucceeded(result.safeResultSummaryJson(), null,
                            result.resultVersionToken(), result.auditRef(), result.outboxRef());
                    execution.recordStepSucceeded();
                    log.info("[AiActionOrchestrator] Step {} succeeded — plan={}", step.toolCode(), plan.id());
                } else if (result.status() == AiActionToolResult.Status.SKIPPED) {
                    stepExec.markSkipped();
                    execution.recordStepSkipped();
                } else {
                    stepExec.markFailed(result.errorCode(), Boolean.TRUE.equals(result.retryable()));
                    execution.recordStepFailed();
                    anyFailed = true;
                    log.warn("[AiActionOrchestrator] Step {} failed: {} — plan={}", step.toolCode(), result.errorCode(), plan.id());
                }
            } catch (Exception e) {
                stepExec.markFailed("ADAPTER_ERROR", false);
                execution.recordStepFailed();
                anyFailed = true;
                log.warn("[AiActionOrchestrator] Step {} threw: {} — plan={}", step.toolCode(), e.getMessage(), e);
            }

            stepExecutionRepository.save(stepExec);
            executionRepository.save(execution);
        }

        if (anyFailed) {
            if (execution.succeededCount() > 0) {
                execution.markPartial();
                plan.markPartial();
            } else {
                execution.markFailed();
                plan.markFailed();
            }
        } else {
            execution.markSucceeded();
            plan.markCompleted();
        }
        executionRepository.save(execution);
        planRepository.save(plan);

        log.info("[AiActionOrchestrator] Execution {} finished — status={}", executionId, execution.status());
    }

    private Map<String, Object> resolveInputArgs(List<AiActionRequestedAction> actions, int stepOrdinal) {
        int idx = stepOrdinal - 1;
        if (idx >= 0 && idx < actions.size()) {
            Map<String, Object> args = actions.get(idx).inputArguments();
            return args != null ? args : Map.of();
        }
        return Map.of();
    }

    private List<AiActionRequestedAction> parseRequestedActions(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("[AiActionOrchestrator] Failed to parse requestedActionsJson: {}", e.getMessage());
            return List.of();
        }
    }
}
