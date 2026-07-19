package com.company.scopery.modules.integrationhub.dashboard.application.service;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.dashboard.application.response.IntegrationDashboardResponse;
import com.company.scopery.modules.integrationhub.deadletter.domain.model.DeadLetterEventRepository;
import com.company.scopery.modules.integrationhub.exportjob.domain.model.ExportJobRepository;
import com.company.scopery.modules.integrationhub.importjob.domain.model.ImportJobRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncJobRepository;
import com.company.scopery.modules.integrationhub.webhook.domain.model.WebhookSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Service
public class IntegrationDashboardService {
    private final IntegrationConnectionRepository connections;
    private final ImportJobRepository imports;
    private final ExportJobRepository exports;
    private final DeadLetterEventRepository deadLetters;
    private final WebhookSubscriptionRepository webhookSubs;
    private final SyncJobRepository syncJobs;
    private final IntegrationAuthorizationService auth;
    public IntegrationDashboardService(IntegrationConnectionRepository connections, ImportJobRepository imports,
            ExportJobRepository exports, DeadLetterEventRepository deadLetters,
            WebhookSubscriptionRepository webhookSubs, SyncJobRepository syncJobs,
            IntegrationAuthorizationService auth) {
        this.connections = connections; this.imports = imports; this.exports = exports;
        this.deadLetters = deadLetters; this.webhookSubs = webhookSubs; this.syncJobs = syncJobs;
        this.auth = auth;
    }
    @Transactional(readOnly = true)
    public IntegrationDashboardResponse getDashboard(UUID workspaceId) {
        auth.requireView(workspaceId);
        var conns = connections.findByWorkspaceId(workspaceId);
        long active = conns.stream().filter(c -> "ACTIVE".equals(c.status())).count();
        long degraded = conns.stream().filter(c -> "DEGRADED".equals(c.status())).count();
        long openDl = deadLetters.findByWorkspaceId(workspaceId).stream().filter(d -> "OPEN".equals(d.status())).count();
        return new IntegrationDashboardResponse(active, degraded,
                imports.findByWorkspaceId(workspaceId).size(),
                exports.findByWorkspaceId(workspaceId).size(),
                openDl,
                webhookSubs.findByWorkspaceId(workspaceId).size(),
                syncJobs.findByWorkspaceId(workspaceId).size());
    }
}
