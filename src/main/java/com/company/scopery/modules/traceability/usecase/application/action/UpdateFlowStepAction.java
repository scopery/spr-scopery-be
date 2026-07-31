package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateFlowStepCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowStepResponse;
import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseFlowStepType;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStepRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateFlowStepAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseFlowRepository flowRepo;
    private final UseCaseFlowStepRepository stepRepo;
    private final FunctionScreenRepository functionScreenRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateFlowStepAction(UseCaseRepository useCaseRepo,
                                UseCaseFlowRepository flowRepo,
                                UseCaseFlowStepRepository stepRepo,
                                FunctionScreenRepository functionScreenRepo,
                                TraceabilityAuthorizationService authorization,
                                TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.flowRepo = flowRepo;
        this.stepRepo = stepRepo;
        this.functionScreenRepo = functionScreenRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseFlowStepResponse execute(UpdateFlowStepCommand c) {
        authorization.requireCreate(c.projectId());

        var useCase = useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        flowRepo.findByIdAndUseCaseId(c.flowId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseFlowNotFound(c.flowId()));

        var step = stepRepo.findByIdAndFlowId(c.stepId(), c.flowId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseFlowStepNotFound(c.stepId()));

        if (c.screenContextId() != null
                && !functionScreenRepo.existsByFunctionIdAndScreenId(useCase.primaryFunctionId(), c.screenContextId())) {
            throw TraceabilityExceptions.useCaseScreenNotLinked(c.screenContextId());
        }

        UseCaseFlowStepType stepType = TraceabilityEnumParser.parseRequired(UseCaseFlowStepType.class, c.stepType(), "stepType");

        var saved = stepRepo.save(step.withUpdated(stepType, c.screenContextId(), c.contentJson(), c.nextScreenId()));

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_FLOW_STEP, saved.id(),
                TraceabilityActivityActions.USE_CASE_FLOW_STEP_UPDATED, "Flow step updated: " + saved.id());

        return UseCaseFlowStepResponse.from(saved);
    }
}
