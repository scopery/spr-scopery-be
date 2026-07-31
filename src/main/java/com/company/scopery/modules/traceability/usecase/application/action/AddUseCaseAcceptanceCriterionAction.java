package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.AddUseCaseAcceptanceCriterionCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseAcceptanceCriterionResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterion;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddUseCaseAcceptanceCriterionAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseAcceptanceCriterionRepository criterionRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public AddUseCaseAcceptanceCriterionAction(UseCaseRepository useCaseRepo,
                                               UseCaseAcceptanceCriterionRepository criterionRepo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.criterionRepo = criterionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseAcceptanceCriterionResponse execute(AddUseCaseAcceptanceCriterionCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        UseCaseAcceptanceCriterion saved = criterionRepo.save(
                UseCaseAcceptanceCriterion.create(c.useCaseId(), c.title(),
                        c.givenText(), c.whenText(), c.thenText(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_ACCEPTANCE_CRITERION, saved.id(),
                TraceabilityActivityActions.USE_CASE_CRITERION_ADDED, "Acceptance criterion added to use case: " + c.useCaseId());

        return UseCaseAcceptanceCriterionResponse.from(saved);
    }
}
