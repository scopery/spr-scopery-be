package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseResponse;
import com.company.scopery.modules.traceability.usecase.domain.enums.UseCaseStatus;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateUseCaseAction {

    private final UseCaseRepository useCaseRepo;
    private final FunctionalItemRepository functionalItems;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateUseCaseAction(UseCaseRepository useCaseRepo,
                               FunctionalItemRepository functionalItems,
                               TraceabilityAuthorizationService authorization,
                               TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.functionalItems = functionalItems;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseResponse execute(UpdateUseCaseCommand c) {
        authorization.requireCreate(c.projectId());

        var uc = useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        UseCaseStatus status = TraceabilityEnumParser.parseRequired(UseCaseStatus.class, c.status(), "status");

        var updated = uc.withUpdated(c.name(), c.goal(), c.primaryActorName(), c.triggerText(), status);
        var saved = useCaseRepo.save(updated);

        var fn = functionalItems.findByIdAndProjectId(saved.primaryFunctionId(), c.projectId());

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE, saved.id(),
                TraceabilityActivityActions.USE_CASE_UPDATED, "Use case updated: " + saved.key());

        return UseCaseResponse.from(saved, fn.map(f -> f.title()).orElse(""));
    }
}
