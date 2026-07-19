package com.company.scopery.modules.integrationhub.sync.application.action;

import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnection;
import com.company.scopery.modules.integrationhub.connection.domain.model.IntegrationConnectionRepository;
import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.shared.error.IntegrationExceptions;
import com.company.scopery.modules.integrationhub.sync.application.adapter.ProviderSyncAdapter;
import com.company.scopery.modules.integrationhub.sync.application.adapter.ProviderSyncResult;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursor;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursorRepository;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRun;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRunRepository;
import com.company.scopery.modules.integrationhub.sync.domain.service.SyncRunService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Runs provider sync skeleton: always persists SyncRun; advances cursor only when adapter allows.
 */
@Component
public class RunProviderSyncAction {

    private final IntegrationConnectionRepository connections;
    private final SyncRunRepository syncRuns;
    private final SyncCursorRepository syncCursors;
    private final IntegrationAuthorizationService auth;
    private final Map<String, ProviderSyncAdapter> adapters;

    public RunProviderSyncAction(
            IntegrationConnectionRepository connections,
            SyncRunRepository syncRuns,
            SyncCursorRepository syncCursors,
            IntegrationAuthorizationService auth,
            List<ProviderSyncAdapter> adapterList) {
        this.connections = connections;
        this.syncRuns = syncRuns;
        this.syncCursors = syncCursors;
        this.auth = auth;
        this.adapters = adapterList.stream()
                .collect(Collectors.toMap(ProviderSyncAdapter::providerCode, Function.identity()));
    }

    @Transactional
    public Map<String, Object> execute(UUID workspaceId, UUID connectionId) {
        auth.requireManage(workspaceId);
        return run(workspaceId, connectionId);
    }

    /**
     * System/job entry — no workspace auth (caller must ensure safe scheduling context).
     */
    @Transactional
    public Map<String, Object> executeAsSystem(UUID workspaceId, UUID connectionId) {
        return run(workspaceId, connectionId);
    }

    private Map<String, Object> run(UUID workspaceId, UUID connectionId) {
        IntegrationConnection connection = connections.findById(connectionId)
                .orElseThrow(() -> IntegrationExceptions.connectionNotFound(connectionId));
        if (!workspaceId.equals(connection.workspaceId())) {
            throw IntegrationExceptions.connectionNotFound(connectionId);
        }

        ProviderSyncAdapter adapter = adapters.get(connection.providerCode());
        ProviderSyncResult result = adapter == null
                ? ProviderSyncResult.unsupported(connection.providerCode(),
                "No sync adapter for provider " + connection.providerCode())
                : adapter.pullStub(connection, null);

        // Stub runs persist as COMPLETED when stub succeeds (honest SYNC_STUB_NO_REMOTE), FAILED when unsupported.
        boolean stubPersistedOk = "SYNC_STUB_NO_REMOTE".equals(result.status());
        SyncRun run = syncRuns.save(
                SyncRun.start(workspaceId, connectionId).complete(result.itemsProcessed(), stubPersistedOk));

        var cursor = syncCursors.findBySyncJobIdAndCursorKey(connectionId, "default")
                .orElseGet(() -> SyncCursor.create(workspaceId, connectionId, "default"));
        if (result.cursorAdvanceAllowed() && result.nextCursorValue() != null) {
            syncCursors.save(SyncRunService.advanceCursorOnSuccess(cursor, result.nextCursorValue(), true));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("syncRunId", run.id());
        response.put("connectionId", connectionId);
        response.put("providerCode", connection.providerCode());
        response.put("status", result.status());
        response.put("syncRunStatus", run.status());
        response.put("message", result.message());
        response.put("liveRemoteCall", result.liveRemoteCall());
        response.put("cursorAdvanceAllowed", result.cursorAdvanceAllowed());
        response.put("itemsProcessed", result.itemsProcessed());
        return response;
    }
}
