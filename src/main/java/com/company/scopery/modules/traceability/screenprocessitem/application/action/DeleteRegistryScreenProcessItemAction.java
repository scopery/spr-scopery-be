package com.company.scopery.modules.traceability.screenprocessitem.application.action;

import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteRegistryScreenProcessItemAction {

    private final RegistryScreenProcessItemRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public DeleteRegistryScreenProcessItemAction(RegistryScreenProcessItemRepository repo,
                                                 TraceabilityAuthorizationService authorization,
                                                 TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID workspaceId, UUID processItemId) {
        authorization.requireWorkspaceCreate(workspaceId);

        repo.findByIdAndWorkspaceId(processItemId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.screenProcessItemNotFound(processItemId));

        repo.deleteByIdAndWorkspaceId(processItemId, workspaceId);

        activityLogger.logSuccess(TraceabilityEntityTypes.SCREEN_PROCESS_ITEM, processItemId,
                TraceabilityActivityActions.SCREEN_PROCESS_ITEM_DELETED,
                "Screen process item deleted: " + processItemId);
    }
}
