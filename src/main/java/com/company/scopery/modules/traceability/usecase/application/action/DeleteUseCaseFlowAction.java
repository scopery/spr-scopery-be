package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseFlowCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteUseCaseFlowAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseFlowRepository flowRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteUseCaseFlowAction(UseCaseRepository useCaseRepo,
                                   UseCaseFlowRepository flowRepo,
                                   TraceabilityAuthorizationService authorization,
                                   TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.flowRepo = flowRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteUseCaseFlowCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        var flow = flowRepo.findByIdAndUseCaseId(c.flowId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseFlowNotFound(c.flowId()));

        flowRepo.deleteByIdAndUseCaseId(c.flowId(), c.useCaseId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_FLOW, flow.id(),
                TraceabilityActivityActions.USE_CASE_FLOW_DELETED, "Use case flow deleted: " + flow.name());
    }
}
