package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseConditionCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseConditionResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseConditionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateUseCaseConditionAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseConditionRepository conditionRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateUseCaseConditionAction(UseCaseRepository useCaseRepo,
                                        UseCaseConditionRepository conditionRepo,
                                        TraceabilityAuthorizationService authorization,
                                        TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.conditionRepo = conditionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseConditionResponse execute(UpdateUseCaseConditionCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        var condition = conditionRepo.findByIdAndUseCaseId(c.conditionId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseConditionNotFound(c.conditionId()));

        var saved = conditionRepo.save(condition.withUpdated(c.content(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_CONDITION, saved.id(),
                TraceabilityActivityActions.USE_CASE_CONDITION_UPDATED, "Condition updated: " + saved.id());

        return UseCaseConditionResponse.from(saved);
    }
}
