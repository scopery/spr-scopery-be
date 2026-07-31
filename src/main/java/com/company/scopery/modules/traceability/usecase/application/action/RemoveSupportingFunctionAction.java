package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.RemoveSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseSupportingFunctionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveSupportingFunctionAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseSupportingFunctionRepository supFnRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public RemoveSupportingFunctionAction(UseCaseRepository useCaseRepo,
                                          UseCaseSupportingFunctionRepository supFnRepo,
                                          TraceabilityAuthorizationService authorization,
                                          TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.supFnRepo = supFnRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(RemoveSupportingFunctionCommand c) {
        authorization.requireCreate(c.projectId());

        var useCase = useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        if (!supFnRepo.exists(c.useCaseId(), c.functionId())) {
            throw TraceabilityExceptions.useCaseSupportingFnNotFound(c.useCaseId(), c.functionId());
        }

        supFnRepo.unlink(c.useCaseId(), c.functionId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE, useCase.id(),
                TraceabilityActivityActions.USE_CASE_SUPPORTING_FN_REMOVED,
                "Supporting function removed from use case: " + c.functionId());
    }
}
