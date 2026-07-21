package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.command.ConfirmAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.response.AiActionConfirmationResponse;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionConfirmationDecision;
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

import java.time.Instant;

@Component
public class ConfirmAiActionPlanAction {

    private final AiActionPlanRepository planRepository;
    private final AiActionConfirmationRepository confirmationRepository;
    private final AiActionActivityLogger activityLogger;

    public ConfirmAiActionPlanAction(AiActionPlanRepository planRepository,
                                     AiActionConfirmationRepository confirmationRepository,
                                     AiActionActivityLogger activityLogger) {
        this.planRepository = planRepository;
        this.confirmationRepository = confirmationRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiActionConfirmationResponse execute(ConfirmAiActionPlanCommand command) {
        AiActionPlan plan = planRepository.findById(command.planId())
                .orElseThrow(() -> AiActionExceptions.planNotFound(command.planId()));

        if (plan.status() != AiActionPlanStatus.WAITING_CONFIRMATION
                && plan.status() != AiActionPlanStatus.PREVIEW_READY) {
            throw AiActionExceptions.planInvalidStatus(plan.id(), plan.status().name());
        }

        if (plan.isExpired()) {
            throw AiActionExceptions.planExpired(plan.id());
        }

        if (!plan.planHash().equals(command.planHash())) {
            throw AiActionExceptions.confirmationInvalid();
        }

        if (plan.version() != command.expectedPlanVersion()) {
            throw AiActionExceptions.confirmationInvalid();
        }

        AiActionConfirmationDecision decision = AiActionConfirmationDecision.valueOf(command.decision());

        AiActionConfirmation confirmation = AiActionConfirmation.create(
                plan.id(), plan.version(), plan.planHash(),
                command.actorId(), decision, command.channel(), command.comment(),
                command.idempotencyKey(), Instant.now().plusSeconds(3600));

        AiActionConfirmation saved = confirmationRepository.save(confirmation);

        if (decision == AiActionConfirmationDecision.CONFIRM) {
            plan.markConfirmed();
            planRepository.save(plan);
            activityLogger.logSuccess(AiActionEntityTypes.PLAN, plan.id(),
                    AiActionActivityActions.CONFIRM_PLAN, "Plan confirmed: " + plan.id());
        } else {
            activityLogger.logSuccess(AiActionEntityTypes.PLAN, plan.id(),
                    AiActionActivityActions.REJECT_PLAN, "Plan rejected: " + plan.id());
        }

        return new AiActionConfirmationResponse(
                saved.id(), saved.planId(), saved.planHash(),
                saved.decision().name(), saved.status().name(),
                saved.expiresAt(), saved.createdAt());
    }
}
