package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseAcceptanceCriterionCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseAcceptanceCriterionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteUseCaseAcceptanceCriterionAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseAcceptanceCriterionRepository criterionRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteUseCaseAcceptanceCriterionAction(UseCaseRepository useCaseRepo,
                                                  UseCaseAcceptanceCriterionRepository criterionRepo,
                                                  TraceabilityAuthorizationService authorization,
                                                  TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.criterionRepo = criterionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteUseCaseAcceptanceCriterionCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        var criterion = criterionRepo.findByIdAndUseCaseId(c.criterionId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseCriterionNotFound(c.criterionId()));

        criterionRepo.deleteByIdAndUseCaseId(c.criterionId(), c.useCaseId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_ACCEPTANCE_CRITERION, criterion.id(),
                TraceabilityActivityActions.USE_CASE_CRITERION_DELETED, "Acceptance criterion deleted: " + criterion.id());
    }
}
