package com.company.scopery.modules.integrationhub.sync.application.action;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncConflictResponse;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncConflictRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class DismissSyncConflictAction {
    private final SyncConflictRepository conflicts;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public DismissSyncConflictAction(SyncConflictRepository conflicts, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.conflicts = conflicts; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public SyncConflictResponse execute(UUID workspaceId, UUID conflictId) {
        auth.requireManage(workspaceId);
        var c = conflicts.findById(conflictId).orElseThrow(() -> IntegrationExceptions.syncConflictNotFound(conflictId));
        if (!workspaceId.equals(c.workspaceId())) throw IntegrationExceptions.syncConflictNotFound(conflictId);
        var saved = conflicts.save(c.dismiss());
        activity.logSuccess("INTEGRATION_SYNC_CONFLICT", saved.id(), "INTEGRATION_SYNC_CONFLICT_DISMISSED", "Sync conflict dismissed");
        return SyncConflictResponse.from(saved);
    }
}
