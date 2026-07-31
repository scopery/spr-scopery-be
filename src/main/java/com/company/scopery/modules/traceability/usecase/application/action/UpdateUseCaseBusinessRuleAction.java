package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.UpdateUseCaseBusinessRuleCommand;
import com.company.scopery.modules.traceability.usecase.application.response.UseCaseBusinessRuleResponse;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseBusinessRuleRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.UseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateUseCaseBusinessRuleAction {

    private final UseCaseRepository useCaseRepo;
    private final UseCaseBusinessRuleRepository ruleRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateUseCaseBusinessRuleAction(UseCaseRepository useCaseRepo,
                                           UseCaseBusinessRuleRepository ruleRepo,
                                           TraceabilityAuthorizationService authorization,
                                           TraceabilityActivityLogger activityLogger) {
        this.useCaseRepo = useCaseRepo;
        this.ruleRepo = ruleRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public UseCaseBusinessRuleResponse execute(UpdateUseCaseBusinessRuleCommand c) {
        authorization.requireCreate(c.projectId());

        useCaseRepo.findByIdAndProjectId(c.useCaseId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseNotFound(c.useCaseId()));

        var rule = ruleRepo.findByIdAndUseCaseId(c.ruleId(), c.useCaseId())
                .orElseThrow(() -> TraceabilityExceptions.useCaseBusinessRuleNotFound(c.ruleId()));

        var saved = ruleRepo.save(rule.withUpdated(c.ruleCode(), c.description(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.USE_CASE_BUSINESS_RULE, saved.id(),
                TraceabilityActivityActions.USE_CASE_BUSINESS_RULE_UPDATED, "Business rule updated: " + saved.id());

        return UseCaseBusinessRuleResponse.from(saved);
    }
}
