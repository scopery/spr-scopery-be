package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.command.ExecuteAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.port.AiActionRequestedAction;
import com.company.scopery.modules.aiaction.application.port.AiActionToolAdapter;
import com.company.scopery.modules.aiaction.application.port.AiActionToolRegistryPort;
import com.company.scopery.modules.aiaction.application.port.AiActionToolResult;
import com.company.scopery.modules.aiaction.application.response.AiActionExecutionResponse;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecutionRepository;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionPlanStatus;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmation;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmationRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStep;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionStepRepository;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequestRepository;
import com.company.scopery.modules.aiaction.shared.activity.AiActionActivityLogger;
import com.company.scopery.modules.aiaction.shared.constant.AiActionActivityActions;
import com.company.scopery.modules.aiaction.shared.constant.AiActionEntityTypes;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
public class ExecuteAiActionPlanAction {

    private static final Logger log = LoggerFactory.getLogger(ExecuteAiActionPlanAction.class);

    private final AiActionPlanRepository planRepository;
    private final AiActionConfirmationRepository confirmationRepository;
    private final AiActionExecutionRepository executionRepository;
    private final AiActionStepRepository stepRepository;
    private final AiActionStepExecutionRepository stepExecutionRepository;
    private final AiActionRequestRepository requestRepository;
    private final AiActionToolRegistryPort toolRegistryPort;
    private final AiActionActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public ExecuteAiActionPlanAction(AiActionPlanRepository planRepository,
                                     AiActionConfirmationRepository confirmationRepository,
                                     AiActionExecutionRepository executionRepository,
                                     AiActionStepRepository stepRepository,
                                     AiActionStepExecutionRepository stepExecutionRepository,
                                     AiActionRequestRepository requestRepository,
                                     AiActionToolRegistryPort toolRegistryPort,
                                     AiActionActivityLogger activityLogger,
                                     ObjectMapper objectMapper) {
        this.planRepository = planRepository;
        this.confirmationRepository = confirmationRepository;
        this.executionRepository = executionRepository;
        this.stepRepository = stepRepository;
        this.stepExecutionRepository = stepExecutionRepository;
        this.requestRepository = requestRepository;
        this.toolRegistryPort = toolRegistryPort;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiActionExecutionResponse execute(ExecuteAiActionPlanCommand command) {
        AiActionPlan plan = planRepository.findById(command.planId())
                .orElseThrow(() -> AiActionExceptions.planNotFound(command.planId()));

        if (plan.status() != AiActionPlanStatus.CONFIRMED
                && plan.status() != AiActionPlanStatus.PREVIEW_READY) {
            throw AiActionExceptions.planInvalidStatus(plan.id(), plan.status().name());
        }

        if (plan.isExpired()) {
            throw AiActionExceptions.planExpired(plan.id());
        }

        if (!plan.planHash().equals(command.planHash())) {
            throw AiActionExceptions.confirmationInvalid();
        }

        if (plan.requiresConfirmation() && command.confirmationId() != null) {
            AiActionConfirmation confirmation = confirmationRepository.findById(command.confirmationId())
                    .orElseThrow(() -> AiActionExceptions.confirmationRequired(plan.id()));
            if (confirmation.isExpired()) {
                throw AiActionExceptions.confirmationExpired(confirmation.id());
            }
        }

        // Idempotent: if execution already exists, return it
        return executionRepository.findByPlanId(plan.id())
                .map(this::toResponse)
                .orElseGet(() -> {
                    String executionKey = "exec:" + plan.id() + ":" + command.idempotencyKey();
                    AiActionExecution execution = AiActionExecution.create(plan.id(), command.actorId(), executionKey);
                    execution.claimLease("inline", Instant.now().plusSeconds(300));
                    AiActionExecution saved = executionRepository.save(execution);

                    plan.markExecutionQueued();
                    planRepository.save(plan);

                    runStepsInline(saved, plan);

                    activityLogger.logSuccess(AiActionEntityTypes.EXECUTION, saved.id(),
                            AiActionActivityActions.QUEUE_EXECUTION, "Execution completed for plan: " + plan.id());

                    return toResponse(saved);
                });
    }

    private void runStepsInline(AiActionExecution execution, AiActionPlan plan) {
        AiActionRequest request = requestRepository.findById(plan.requestId()).orElse(null);
        List<AiActionRequestedAction> requestedActions = parseRequestedActions(
                request != null ? request.requestedActionsJson() : null);
        List<AiActionStep> steps = stepRepository.findByPlanIdOrderByOrdinal(plan.id());

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
                    log.info("[AiActionExecute] Step {} succeeded — plan={}", step.toolCode(), plan.id());
                } else if (result.status() == AiActionToolResult.Status.SKIPPED) {
                    stepExec.markSkipped();
                    execution.recordStepSkipped();
                } else {
                    stepExec.markFailed(result.errorCode(), Boolean.TRUE.equals(result.retryable()));
                    execution.recordStepFailed();
                    anyFailed = true;
                    log.warn("[AiActionExecute] Step {} failed: {} — plan={}", step.toolCode(), result.errorCode(), plan.id());
                }
            } catch (Exception e) {
                stepExec.markFailed("ADAPTER_ERROR", false);
                execution.recordStepFailed();
                anyFailed = true;
                log.warn("[AiActionExecute] Step {} threw exception: {} — plan={}", step.toolCode(), e.getMessage(), e);
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
    }

    private Map<String, Object> resolveInputArgs(List<AiActionRequestedAction> actions, int stepOrdinal) {
        // Steps are 1-indexed; requestedActions list is 0-indexed
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
            log.warn("[AiActionExecute] Failed to parse requestedActionsJson: {}", e.getMessage());
            return List.of();
        }
    }

    private AiActionExecutionResponse toResponse(AiActionExecution e) {
        return new AiActionExecutionResponse(
                e.id(), e.planId(), e.status().name(), e.executionVersion(),
                e.currentStepOrdinal(), e.succeededCount(), e.failedCount(),
                e.skippedCount(), e.compensatedCount(), e.cancelledCount(),
                e.startedAt(), e.completedAt(), e.createdAt());
    }
}
