package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.ReorderFlowStepsCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseFlowStepResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStep;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseFlowStepRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReorderFlowStepsAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseFlowRepository flowRepo;
    private final UseCaseFlowStepRepository stepRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public ReorderFlowStepsAction(UseCaseRepository useCaseRepo,
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
    public List<UseCaseFlowStepResponse> execute(ReorderFlowStepsCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        flowRepo.findByIdAndUseCaseId(c.flowId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseFlowNotFound(c.flowId()));

        Map<UUID, UseCaseFlowStep> stepsById = stepRepo.findByFlowIdOrderByDisplayOrder(c.flowId())
                .stream().collect(Collectors.toMap(UseCaseFlowStep::id, s -> s));

        List<UseCaseFlowStep> reordered = new ArrayList<>();
        for (int idx = 0; idx < c.stepIds().size(); idx++) {
            UUID stepId = c.stepIds().get(idx);
            UseCaseFlowStep step = stepsById.get(stepId);
            if (step != null) {
                reordered.add(step.withDisplayOrder(idx));
            }
        }

        List<UseCaseFlowStepResponse> result = stepRepo.saveAll(reordered)
                .stream().map(UseCaseFlowStepResponse::from).toList();

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_FLOW_STEP, c.flowId(),
                TraceabilityActivityActions.USE_CASE_FLOW_STEPS_REORDERED, "Flow steps reordered for flow: " + c.flowId());

        return result;
    }
}
