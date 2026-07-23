package com.company.scopery.modules.traceability.screencomponent.application.action;

import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponentRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UnlinkScreenComponentAction {

    private final ScreenComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UnlinkScreenComponentAction(ScreenComponentRepository repo,
                                       TraceabilityAuthorizationService authorization,
                                       TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID screenId, UUID componentId) {
        authorization.requireWorkspaceCreate(workspaceId);

        if (!repo.existsByScreenIdAndComponentId(screenId, componentId)) {
            throw TraceabilityExceptions.screenComponentNotFound(screenId, componentId);
        }

        repo.deleteByScreenIdAndComponentId(screenId, componentId);

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_COMPONENT, screenId,
                TraceabilityActivityActions.SCREEN_COMPONENT_UNLINKED, "Component unlinked from screen");
    }
}
