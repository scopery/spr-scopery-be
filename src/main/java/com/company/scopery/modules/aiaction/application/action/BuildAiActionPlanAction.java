package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.command.BuildAiActionPlanCommand;
import com.company.scopery.modules.aiaction.application.orchestrator.AiActionPlanningOrchestrator;
import com.company.scopery.modules.aiaction.application.response.AiActionPlanResponse;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlan;
import com.company.scopery.modules.aiaction.plan.domain.model.AiActionPlanRepository;
import com.company.scopery.modules.aiaction.request.domain.enums.AiActionRequestStatus;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequest;
import com.company.scopery.modules.aiaction.request.domain.model.AiActionRequestRepository;
import com.company.scopery.modules.aiaction.shared.activity.AiActionActivityLogger;
import com.company.scopery.modules.aiaction.shared.constant.AiActionActivityActions;
import com.company.scopery.modules.aiaction.shared.constant.AiActionEntityTypes;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BuildAiActionPlanAction {

    private final AiActionRequestRepository requestRepository;
    private final AiActionPlanRepository planRepository;
    private final AiActionPlanningOrchestrator planningOrchestrator;
    private final AiActionActivityLogger activityLogger;

    public BuildAiActionPlanAction(AiActionRequestRepository requestRepository,
                                    AiActionPlanRepository planRepository,
                                    AiActionPlanningOrchestrator planningOrchestrator,
                                    AiActionActivityLogger activityLogger) {
        this.requestRepository = requestRepository;
        this.planRepository = planRepository;
        this.planningOrchestrator = planningOrchestrator;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiActionPlanResponse execute(BuildAiActionPlanCommand command) {
        AiActionRequest request = requestRepository.findById(command.requestId())
                .orElseThrow(() -> AiActionExceptions.requestNotFound(command.requestId()));

        if (request.status() != AiActionRequestStatus.RECEIVED
                && request.status() != AiActionRequestStatus.PLANNING) {
            throw AiActionExceptions.planInvalidStatus(null, request.status().name());
        }

        request.markPlanning();
        requestRepository.save(request);

        AiActionPlan plan;
        try {
            plan = planningOrchestrator.plan(request, command.policyCode());
        } catch (Exception e) {
            request.markFailed();
            requestRepository.save(request);
            throw e;
        }

        request.markPlanned(plan.id());
        requestRepository.save(request);

        activityLogger.logSuccess(AiActionEntityTypes.PLAN, plan.id(),
                AiActionActivityActions.BUILD_PLAN, "AI action plan built for request: " + request.id());

        return toPlanResponse(plan);
    }

    private AiActionPlanResponse toPlanResponse(AiActionPlan p) {
        return new AiActionPlanResponse(
                p.id(), p.requestId(), p.planNumber(), p.status().name(),
                p.planHash(), p.version(), p.summary(),
                p.riskLevel() != null ? p.riskLevel().name() : null,
                p.executionMode() != null ? p.executionMode().name() : null,
                p.requiresConfirmation(), p.stepCount(), p.targetCount(),
                p.expiresAt(), p.createdAt());
    }
}
