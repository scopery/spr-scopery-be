package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.AddSupportingFunctionCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseSupportingFunctionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddSupportingFunctionAction {

    private final UseCaseRepository useCaseRepo;
    private final FunctionalItemRepository functionalItems;
    private final UseCaseSupportingFunctionRepository supFnRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public AddSupportingFunctionAction(UseCaseRepository useCaseRepo,
                                       FunctionalItemRepository functionalItems,
                                       UseCaseSupportingFunctionRepository supFnRepo,
                                       TraceabilityAuthorizationService authorization,
                                       TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.functionalItems = functionalItems;
        this.supFnRepo = supFnRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(AddSupportingFunctionCommand c) {
        authorization.requireCreate(c.projectId());

        var useCase = useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        functionalItems.findByIdAndProjectId(c.functionId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.functionId()));

        if (supFnRepo.exists(c.useCaseId(), c.functionId())) {
            throw TraceabilityExceptions.useCaseSupportingFnDuplicate();
        }

        supFnRepo.link(c.useCaseId(), c.functionId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE, useCase.id(),
                TraceabilityActivityActions.USE_CASE_SUPPORTING_FN_ADDED,
                "Supporting function added to use case: " + c.functionId());
    }
}
