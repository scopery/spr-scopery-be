package com.company.scopery.modules.traceability.componentfield.application.action;

import com.company.scopery.modules.traceability.componentfield.domain.model.RegistryComponentFieldRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Component
public class DeleteRegistryComponentFieldAction {

    private final RegistryComponentFieldRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryComponentFieldAction(RegistryComponentFieldRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID fieldId) {
        authorization.requireWorkspaceCreate(workspaceId);
        var field = repo.findByIdAndWorkspaceId(fieldId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.componentFieldNotFound(fieldId));
        repo.delete(field.id());
        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_FIELD, fieldId,
                TraceabilityActivityActions.COMPONENT_FIELD_DELETED,
                "Component field deleted: " + field.fieldKey());
    }
}
