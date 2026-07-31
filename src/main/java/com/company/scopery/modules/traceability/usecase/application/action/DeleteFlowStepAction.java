package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteFlowStepCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStepRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteFlowStepAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseFlowRepository flowRepo;
    private final UseCaseFlowStepRepository stepRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteFlowStepAction(UseCaseRepository useCaseRepo,
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
    public void execute(DeleteFlowStepCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        flowRepo.findByIdAndUseCaseId(c.flowId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseFlowNotFound(c.flowId()));

        var step = stepRepo.findByIdAndFlowId(c.stepId(), c.flowId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseFlowStepNotFound(c.stepId()));

        stepRepo.deleteByIdAndFlowId(c.stepId(), c.flowId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_FLOW_STEP, step.id(),
                TraceabilityActivityActions.USE_CASE_FLOW_STEP_DELETED, "Flow step deleted: " + step.id());
    }
}
