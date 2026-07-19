package com.company.scopery.modules.integrationhub.sync.infrastructure.mapper;

import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursor;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRun;
import com.company.scopery.modules.integrationhub.sync.infrastructure.persistence.SyncCursorJpaEntity;
import com.company.scopery.modules.integrationhub.sync.infrastructure.persistence.SyncRunJpaEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SyncPersistenceMapper {
    public SyncRunJpaEntity toJpaEntity(SyncRun d) {
        SyncRunJpaEntity e = new SyncRunJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setSyncJobId(d.syncJobId());
        e.setStatus(d.status());
        e.setStartedAt(d.startedAt());
        e.setCompletedAt(d.completedAt());
        e.setProcessedCount(d.processedCount());
        e.setVersion(d.version());
        e.setCreatedAt(d.createdAt());
        return e;
    }

    public SyncRun toDomain(SyncRunJpaEntity e) {
        return new SyncRun(
                e.getId(), e.getWorkspaceId(), e.getSyncJobId(), e.getStatus(),
                e.getStartedAt(), e.getCompletedAt(),
                e.getProcessedCount() == null ? 0 : e.getProcessedCount(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }

    public SyncCursorJpaEntity toJpaEntity(SyncCursor d) {
        SyncCursorJpaEntity e = new SyncCursorJpaEntity();
        e.setId(d.id());
        e.setWorkspaceId(d.workspaceId());
        e.setSyncJobId(d.syncJobId());
        e.setCursorKey(d.cursorKey());
        e.setCursorValue(d.cursorValue());
        e.setLastSuccessfulSyncAt(d.lastSuccessfulSyncAt());
        e.setVersion(d.version());
        Instant now = Instant.now();
        e.setCreatedAt(d.lastSuccessfulSyncAt() == null ? now : d.lastSuccessfulSyncAt());
        e.setUpdatedAt(now);
        return e;
    }

    public SyncCursor toDomain(SyncCursorJpaEntity e) {
        return new SyncCursor(
                e.getId(), e.getWorkspaceId(), e.getSyncJobId(), e.getCursorKey(), e.getCursorValue(),
                e.getLastSuccessfulSyncAt(), e.getVersion() == null ? 0 : e.getVersion());
    }
}
