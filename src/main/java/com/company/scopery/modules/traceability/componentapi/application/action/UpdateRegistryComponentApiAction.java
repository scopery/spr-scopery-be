package com.company.scopery.modules.traceability.componentapi.application.action;

import com.company.scopery.modules.traceability.componentapi.application.command.UpdateRegistryComponentApiCommand;
import com.company.scopery.modules.traceability.componentapi.application.response.RegistryComponentApiResponse;
import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApiRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateRegistryComponentApiAction {

    private final RegistryComponentApiRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UpdateRegistryComponentApiAction(RegistryComponentApiRepository repo,
                                             TraceabilityAuthorizationService authorization,
                                             TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryComponentApiResponse execute(UpdateRegistryComponentApiCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        var existing = repo.findByIdAndWorkspaceId(c.componentApiId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.componentApiNotFound(c.componentApiId()));

        var updated = repo.save(existing.withUpdated(c.role(), c.note(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_API, updated.id(),
                TraceabilityActivityActions.COMPONENT_API_UPDATED, "Component API link updated");

        return RegistryComponentApiResponse.from(updated);
    }
}
