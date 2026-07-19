package com.company.scopery.modules.integrationhub.sync.application.jobs;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.sync.application.action.RunProviderSyncAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Periodic stub sync for Slack / Drive / Jira ACTIVE connections.
 * Persists SyncRun only; never claims live remote sync success.
 */
@Component
public class ProviderSyncStubJob {

    private static final Logger log = LoggerFactory.getLogger(ProviderSyncStubJob.class);
    private static final Set<String> STUB_PROVIDERS = Set.of("SLACK", "GOOGLE_DRIVE", "JIRA");

    private final IntegrationConnectionRepository connections;
    private final RunProviderSyncAction runProviderSync;

    public ProviderSyncStubJob(IntegrationConnectionRepository connections, RunProviderSyncAction runProviderSync) {
        this.connections = connections;
        this.runProviderSync = runProviderSync;
    }

    @Scheduled(cron = "${scopery.integration.provider-sync-stub-cron:0 20 * * * *}")
    public void runStubSyncs() {
        int ran = 0;
        for (IntegrationConnection connection : connections.findByStatus("ACTIVE")) {
            if (!STUB_PROVIDERS.contains(connection.providerCode())) {
                continue;
            }
            try {
                runProviderSync.executeAsSystem(connection.workspaceId(), connection.id());
                ran++;
            } catch (Exception ex) {
                log.warn("Provider sync stub failed for connection {}: {}", connection.id(), ex.getMessage());
            }
        }
        if (ran > 0) {
            log.info("Provider sync stub job completed for {} connection(s) (no live remote calls)", ran);
        }
    }
}
