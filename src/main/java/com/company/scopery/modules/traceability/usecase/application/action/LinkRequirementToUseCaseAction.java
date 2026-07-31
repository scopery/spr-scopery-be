package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementUseCaseRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkRequirementToUseCaseAction {

    private final UseCaseRepository useCaseRepo;
    private final RequirementUseCaseRepository requirementUseCaseRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkRequirementToUseCaseAction(UseCaseRepository useCaseRepo,
                                          RequirementUseCaseRepository requirementUseCaseRepo,
                                          TraceabilityAuthorizationService authorization,
                                          TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.requirementUseCaseRepo = requirementUseCaseRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(LinkRequirementToUseCaseCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        if (requirementUseCaseRepo.exists(c.requirementId(), c.useCaseId())) {
            throw TraceabilityExceptions.requirementUseCaseDuplicate();
        }

        requirementUseCaseRepo.link(c.requirementId(), c.useCaseId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE, c.useCaseId(),
                TraceabilityActivityActions.REQUIREMENT_USE_CASE_LINKED,
                "Requirement linked to use case: " + c.requirementId());
    }
}
