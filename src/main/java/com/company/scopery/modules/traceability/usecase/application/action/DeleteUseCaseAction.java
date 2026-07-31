package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.DeleteUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteUseCaseAction {

    private final UseCaseRepository useCaseRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteUseCaseAction(UseCaseRepository useCaseRepo,
                               TraceabilityAuthorizationService authorization,
                               TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(DeleteUseCaseCommand c) {
        authorization.requireCreate(c.projectId());

        var uc = useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        useCaseRepo.deleteByIdAndProjectId(c.useCaseId(), c.projectId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE, uc.id(),
                TraceabilityActivityActions.USE_CASE_DELETED, "Use case deleted: " + uc.key());
    }
}
