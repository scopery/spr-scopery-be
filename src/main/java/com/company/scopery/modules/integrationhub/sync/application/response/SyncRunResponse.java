package com.company.scopery.modules.integrationhub.sync.application.response;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRun;
import java.time.Instant; import java.util.UUID;
public record SyncRunResponse(UUID id, UUID syncJobId, String status, Instant startedAt, Instant completedAt,
        long processedCount, Instant createdAt) {
    public static SyncRunResponse from(SyncRun r) {
        return new SyncRunResponse(r.id(), r.syncJobId(), r.status(), r.startedAt(), r.completedAt(),
                r.processedCount(), r.createdAt());
    }
}
