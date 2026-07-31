package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseFlowCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowStepResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStepRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UpdateUseCaseFlowAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseFlowRepository flowRepo;
    private final UseCaseFlowStepRepository stepRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateUseCaseFlowAction(UseCaseRepository useCaseRepo,
                                   UseCaseFlowRepository flowRepo,
                                   UseCaseFlowStepRepository stepRepo,
                                   TraceabilityAuthorizationService authorization,
                                   TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.flowRepo = flowRepo;
        this.stepRepo = stepRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseFlowResponse execute(UpdateUseCaseFlowCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        var flow = flowRepo.findByIdAndUseCaseId(c.flowId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseFlowNotFound(c.flowId()));

        var saved = flowRepo.save(flow.withUpdated(c.name(), c.sourceStepId(), c.conditionText()));

        List<UseCaseFlowStepResponse> steps = stepRepo.findByFlowIdOrderByDisplayOrder(saved.id())
                .stream().map(UseCaseFlowStepResponse::from).toList();

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_FLOW, saved.id(),
                TraceabilityActivityActions.USE_CASE_FLOW_UPDATED, "Use case flow updated: " + saved.name());

        return UseCaseFlowResponse.from(saved, steps);
    }
}
