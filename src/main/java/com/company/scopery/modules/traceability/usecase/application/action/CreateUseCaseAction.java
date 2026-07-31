package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.CreateUseCaseCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseDetailResponse;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCase;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CreateUseCaseAction {

    private final UseCaseRepository useCaseRepo;
    private final FunctionalItemRepository functionalItems;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateUseCaseAction(UseCaseRepository useCaseRepo,
                               FunctionalItemRepository functionalItems,
                               TraceabilityAuthorizationService authorization,
                               TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.functionalItems = functionalItems;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseDetailResponse execute(CreateUseCaseCommand c) {
        authorization.requireCreate(c.projectId());

        var fn = functionalItems.findByIdAndProjectId(c.primaryFunctionId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.primaryFunctionId()));

        if (useCaseRepo.existsByProjectIdAndKey(c.projectId(), c.key())) {
            throw TraceabilityExceptions.useCaseKeyExists(c.key());
        }

        UseCase saved = useCaseRepo.save(UseCase.create(
                c.projectId(), c.primaryFunctionId(), c.key(),
                c.name(), c.goal(), c.primaryActorName(), c.triggerText()));

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE, saved.id(),
                TraceabilityActivityActions.USE_CASE_CREATED, "Use case created: " + saved.key());

        return new UseCaseDetailResponse(
                UseCaseResponse.from(saved, fn.title()),
                List.of(), List.of(), List.of(), List.of(), List.of());
    }
}
