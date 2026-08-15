package com.company.scopery.modules.traceability.componentapi.application.action;

import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApiRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryComponentApiAction {

    private final RegistryComponentApiRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryComponentApiAction(RegistryComponentApiRepository repo,
                                             TraceabilityAuthorizationService authorization,
                                             TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID componentApiId) {
        authorization.requireWorkspaceCreate(workspaceId);

        repo.findByIdAndWorkspaceId(componentApiId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.componentApiNotFound(componentApiId));

        repo.delete(componentApiId);

        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_API, componentApiId,
                TraceabilityActivityActions.COMPONENT_API_UNLINKED, "Component API link removed");
    }
}
