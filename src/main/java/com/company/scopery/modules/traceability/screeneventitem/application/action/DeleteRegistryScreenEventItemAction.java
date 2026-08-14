package com.company.scopery.modules.traceability.screeneventitem.application.action;

import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryScreenEventItemAction {

    private final RegistryScreenEventItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryScreenEventItemAction(RegistryScreenEventItemRepository repo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID eventItemId) {
        authorization.requireWorkspaceCreate(workspaceId);

        repo.findByIdAndWorkspaceId(eventItemId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.screenEventItemNotFound(eventItemId));

        repo.deleteByIdAndWorkspaceId(eventItemId, workspaceId);

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_EVENT_ITEM, eventItemId,
                TraceabilityActivityActions.SCREEN_EVENT_ITEM_DELETED,
                "Screen event item deleted: " + eventItemId);
    }
}
