package com.company.scopery.modules.integrationhub.sync.application.action;

import com.company.scopery.modules.integrationhub.shared.authorization.IntegrationAuthorizationService;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursor;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursorRepository;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRun;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRunRepository;
import com.company.scopery.modules.integrationhub.sync.domain.service.SyncRunService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class RunSyncJobAction {
    private final SyncRunRepository syncRuns;
    private final SyncCursorRepository syncCursors;
    private final IntegrationAuthorizationService auth;

    public RunSyncJobAction(SyncRunRepository syncRuns, SyncCursorRepository syncCursors, IntegrationAuthorizationService auth) {
        this.syncRuns = syncRuns;
        this.syncCursors = syncCursors;
        this.auth = auth;
    }

    @Transactional
    public Map<String, Object> execute(UUID workspaceId, UUID syncJobId, boolean success, String nextCursorValue) {
        auth.requireManage(workspaceId);
        var run = syncRuns.save(SyncRun.start(workspaceId, syncJobId).complete(1, success));
        var cursor = syncCursors.findBySyncJobIdAndCursorKey(syncJobId, "default")
                .orElseGet(() -> SyncCursor.create(workspaceId, syncJobId, "default"));
        var advanced = SyncRunService.advanceCursorOnSuccess(cursor, nextCursorValue, success);
        if (advanced.cursorValue() != null) {
            syncCursors.save(advanced);
        }
        return Map.of(
                "syncRunId", run.id(),
                "syncJobId", syncJobId,
                "status", run.status(),
                "cursorValue", advanced.cursorValue() == null ? "" : advanced.cursorValue(),
                "cursorUpdated", advanced.cursorValue() != null);
    }
}
