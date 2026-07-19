package com.company.scopery.modules.integrationhub.sync.application.action;
import com.company.scopery.modules.integrationhub.shared.activity.IntegrationActivityLogger;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import com.company.scopery.modules.integrationhub.sync.application.response.SyncJobResponse;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJobRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class EnableSyncJobAction {
    private final SyncJobRepository jobs;
    private final IntegrationAuthorizationService auth;
    private final IntegrationActivityLogger activity;
    public EnableSyncJobAction(SyncJobRepository jobs, IntegrationAuthorizationService auth, IntegrationActivityLogger activity) {
        this.jobs = jobs; this.auth = auth; this.activity = activity;
    }
    @Transactional
    public SyncJobResponse execute(UUID workspaceId, UUID jobId) {
        auth.requireManage(workspaceId);
        var j = jobs.findById(jobId).orElseThrow(() -> IntegrationExceptions.syncJobNotFound(jobId));
        if (!workspaceId.equals(j.workspaceId())) throw IntegrationExceptions.syncJobNotFound(jobId);
        var saved = jobs.save(j.activate());
        activity.logSuccess("INTEGRATION_SYNC_JOB", saved.id(), "INTEGRATION_SYNC_JOB_ENABLED", "Sync job enabled");
        return SyncJobResponse.from(saved);
    }
}
