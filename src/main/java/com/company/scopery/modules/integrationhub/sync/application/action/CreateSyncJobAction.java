package com.company.scopery.modules.integrationhub.sync.application.action;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncJobResponse;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJob;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJobRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class CreateSyncJobAction {
    private final SyncJobRepository jobs;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public CreateSyncJobAction(SyncJobRepository jobs, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.jobs = jobs; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public SyncJobResponse execute(UUID workspaceId, UUID connectionId, String name, String direction,
            String mode, String scope, String conflictStrategy) {
        auth.requireManage(workspaceId);
        var saved = jobs.save(SyncJob.create(workspaceId, connectionId, name, direction, mode, scope, conflictStrategy));
        activity.logSuccess("INTEGRATION_SYNC_JOB", saved.id(), "INTEGRATION_SYNC_JOB_CREATED", "Sync job created: " + name);
        return SyncJobResponse.from(saved);
    }
}
