package com.company.scopery.modules.traceability.validationruletype.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.validationruletype.application.command.CreateRegistryValidationRuleTypeCommand;
import com.company.scopery.modules.traceability.validationruletype.application.response.RegistryValidationRuleTypeResponse;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import com.company.scopery.modules.traceability.validationruletype.infrastructure.persistence.SpringDataRegistryValidationRuleTypeJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryValidationRuleTypeAction {

    private final RegistryValidationRuleTypeRepository repo;
    private final SpringDataRegistryValidationRuleTypeJpaRepository springData;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryValidationRuleTypeAction(RegistryValidationRuleTypeRepository repo,
                                                   SpringDataRegistryValidationRuleTypeJpaRepository springData,
                                                   TraceabilityAuthorizationService authorization,
                                                   TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.springData = springData;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryValidationRuleTypeResponse execute(CreateRegistryValidationRuleTypeCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        if (springData.existsByCodeAndWorkspaceId(c.code().trim(), c.workspaceId())) {
            throw TraceabilityExceptions.validationRuleTypeCodeExists(c.code().trim());
        }
        RegistryValidationRuleType domain = RegistryValidationRuleType.create(
                c.workspaceId(), c.code().trim(), c.name().trim(), c.category(),
                c.paramSchemaJson(), c.defaultMessage(), c.description(), c.displayOrder());
        RegistryValidationRuleType saved = repo.save(domain);
        activityLogger.logSuccess(TraceabilityEntityTypes.VALIDATION_RULE_TYPE, saved.id(),
                TraceabilityActivityActions.VALIDATION_RULE_TYPE_CREATED,
                "Validation rule type created: " + saved.code());
        return RegistryValidationRuleTypeResponse.from(saved);
    }
}
