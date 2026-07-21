package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.command.CancelAiActionPlanCommand;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionPlanStatus;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.shared.activity.AiActionActivityLogger;
import com.company.scopery.modules.aiaction.shared.constant.AiActionActivityActions;
import com.company.scopery.modules.aiaction.shared.constant.AiActionEntityTypes;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class CancelAiActionPlanAction {

    private static final Set<AiActionPlanStatus> CANCELLABLE = Set.of(
            AiActionPlanStatus.DRAFT, AiActionPlanStatus.VALIDATING,
            AiActionPlanStatus.INVALID, AiActionPlanStatus.PREVIEW_READY,
            AiActionPlanStatus.WAITING_CONFIRMATION, AiActionPlanStatus.CONFIRMED,
            AiActionPlanStatus.EXECUTION_QUEUED);

    private final AiActionPlanRepository planRepository;
    private final AiActionActivityLogger activityLogger;

    public CancelAiActionPlanAction(AiActionPlanRepository planRepository,
                                    AiActionActivityLogger activityLogger) {
        this.planRepository = planRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(CancelAiActionPlanCommand command) {
        AiActionPlan plan = planRepository.findById(command.planId())
                .orElseThrow(() -> AiActionExceptions.planNotFound(command.planId()));

        if (!CANCELLABLE.contains(plan.status())) {
            throw AiActionExceptions.planInvalidStatus(plan.id(), plan.status().name());
        }

        if (plan.version() != command.expectedPlanVersion()) {
            throw AiActionExceptions.confirmationInvalid();
        }

        plan.markCancelled();
        planRepository.save(plan);

        activityLogger.logSuccess(AiActionEntityTypes.PLAN, plan.id(),
                AiActionActivityActions.CANCEL_PLAN, "Plan cancelled: " + plan.id());
    }
}
