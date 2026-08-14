package com.company.scopery.modules.traceability.validationruletype.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.validationruletype.application.command.UpdateRegistryValidationRuleTypeCommand;
import com.company.scopery.modules.traceability.validationruletype.application.response.RegistryValidationRuleTypeResponse;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryValidationRuleTypeAction {

    private final RegistryValidationRuleTypeRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryValidationRuleTypeAction(RegistryValidationRuleTypeRepository repo,
                                                   TraceabilityAuthorizationService authorization,
                                                   TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryValidationRuleTypeResponse execute(UpdateRegistryValidationRuleTypeCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        RegistryValidationRuleType existing = repo.findByIdAndAccessible(c.id(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.validationRuleTypeNotFound(c.id()));
        if (existing.isSystem()) {
            throw new com.company.scopery.common.exception.BusinessException(
                    "System validation rule types cannot be modified");
        }
        RegistryValidationRuleType updated = existing.withUpdated(
                c.name().trim(), c.category(), c.paramSchemaJson(),
                c.defaultMessage(), c.description(), c.displayOrder());
        RegistryValidationRuleType saved = repo.save(updated);
        activityLogger.logSuccess(TraceabilityEntityTypes.VALIDATION_RULE_TYPE, saved.id(),
                TraceabilityActivityActions.VALIDATION_RULE_TYPE_UPDATED,
                "Validation rule type updated: " + saved.code());
        return RegistryValidationRuleTypeResponse.from(saved);
    }
}
