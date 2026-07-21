package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.command.ExecuteAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.response.AiActionExecutionResponse;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionPlanStatus;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmation;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionConfirmationRepository;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.shared.activity.AiActionActivityLogger;
import com.company.scopery.modules.aiaction.shared.constant.AiActionActivityActions;
import com.company.scopery.modules.aiaction.shared.constant.AiActionEntityTypes;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExecuteAiActionPlanAction {

    private final AiActionPlanRepository planRepository;
    private final AiActionConfirmationRepository confirmationRepository;
    private final AiActionExecutionRepository executionRepository;
    private final AiActionActivityLogger activityLogger;

    public ExecuteAiActionPlanAction(AiActionPlanRepository planRepository,
                                     AiActionConfirmationRepository confirmationRepository,
                                     AiActionExecutionRepository executionRepository,
                                     AiActionActivityLogger activityLogger) {
        this.planRepository = planRepository;
        this.confirmationRepository = confirmationRepository;
        this.executionRepository = executionRepository;
        this.activityLogger = activityLogger;
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

        return executionRepository.findByPlanId(plan.id())
                .map(this::toResponse)
                .orElseGet(() -> {
                    String executionKey = "exec:" + plan.id() + ":" + command.idempotencyKey();
                    AiActionExecution execution = AiActionExecution.create(plan.id(), command.actorId(), executionKey);
                    AiActionExecution saved = executionRepository.save(execution);

                    plan.markExecutionQueued();
                    planRepository.save(plan);

                    activityLogger.logSuccess(AiActionEntityTypes.EXECUTION, saved.id(),
                            AiActionActivityActions.QUEUE_EXECUTION, "Execution queued for plan: " + plan.id());

                    return toResponse(saved);
                });
    }

    private AiActionExecutionResponse toResponse(AiActionExecution e) {
        return new AiActionExecutionResponse(
                e.id(), e.planId(), e.status().name(), e.executionVersion(),
                e.currentStepOrdinal(), e.succeededCount(), e.failedCount(),
                e.skippedCount(), e.compensatedCount(), e.cancelledCount(),
                e.startedAt(), e.completedAt(), e.createdAt());
    }
}
