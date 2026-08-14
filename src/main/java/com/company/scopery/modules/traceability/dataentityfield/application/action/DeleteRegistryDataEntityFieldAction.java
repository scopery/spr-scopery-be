package com.company.scopery.modules.traceability.dataentityfield.application.action;

import com.company.scopery.modules.traceability.dataentityfield.domain.model.RegistryDataEntityFieldRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryDataEntityFieldAction {

    private final RegistryDataEntityFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryDataEntityFieldAction(RegistryDataEntityFieldRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceCreate(workspaceId);

        repo.findByIdAndWorkspaceId(fieldId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.dataEntityFieldNotFound(fieldId));

        repo.delete(fieldId, workspaceId);

        activityLogger.logSuccess(TraceabilityEntityTypes.DATA_ENTITY_FIELD, fieldId,
                TraceabilityActivityActions.DATA_ENTITY_FIELD_DELETED,
                "Data entity field deleted: " + fieldId);
    }
}
