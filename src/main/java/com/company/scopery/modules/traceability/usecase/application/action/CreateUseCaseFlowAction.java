package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseFlowCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowResponse;
import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowType;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlow;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CreateUseCaseFlowAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseFlowRepository flowRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateUseCaseFlowAction(UseCaseRepository useCaseRepo,
                                   UseCaseFlowRepository flowRepo,
                                   TraceabilityAuthorizationService authorization,
                                   TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.flowRepo = flowRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseFlowResponse execute(CreateUseCaseFlowCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        UseCaseFlowType flowType = TraceabilityEnumParser.parseRequired(UseCaseFlowType.class, c.flowType(), "flowType");

        if (flowType == UseCaseFlowType.MAIN && flowRepo.existsByUseCaseIdAndFlowType(c.useCaseId(), UseCaseFlowType.MAIN)) {
            throw TraceabilityExceptions.useCaseMainFlowExists(c.useCaseId());
        }

        UseCaseFlow saved = flowRepo.save(UseCaseFlow.create(
                c.useCaseId(), flowType, c.name(), c.sourceStepId(), c.conditionText(), 0));

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_FLOW, saved.id(),
                TraceabilityActivityActions.USE_CASE_FLOW_CREATED, "Use case flow created: " + saved.name());

        return UseCaseFlowResponse.from(saved, List.of());
    }
}
