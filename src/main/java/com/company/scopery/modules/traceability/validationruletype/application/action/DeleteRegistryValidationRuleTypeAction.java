package com.company.scopery.modules.traceability.validationruletype.application.action;

import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleType;
import com.company.scopery.modules.traceability.validationruletype.domain.model.RegistryValidationRuleTypeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryValidationRuleTypeAction {

    private final RegistryValidationRuleTypeRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryValidationRuleTypeAction(RegistryValidationRuleTypeRepository repo,
                                                   TraceabilityAuthorizationService authorization,
                                                   TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID id) {
        authorization.requireWorkspaceCreate(workspaceId);
        RegistryValidationRuleType existing = repo.findByIdAndAccessible(id, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.validationRuleTypeNotFound(id));
        if (existing.isSystem()) {
            throw new com.company.scopery.common.exception.BusinessException(
                    "System validation rule types cannot be deleted");
        }
        repo.delete(id, workspaceId);
        activityLogger.logSuccess(TraceabilityEntityTypes.VALIDATION_RULE_TYPE, id,
                TraceabilityActivityActions.VALIDATION_RULE_TYPE_DELETED,
                "Validation rule type deleted: " + existing.code());
    }
}
