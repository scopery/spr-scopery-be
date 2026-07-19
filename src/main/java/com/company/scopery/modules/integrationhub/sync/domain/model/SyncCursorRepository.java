package com.company.scopery.modules.integrationhub.sync.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface SyncCursorRepository {
    SyncCursor save(SyncCursor cursor);
    Optional<SyncCursor> findBySyncJobIdAndCursorKey(UUID syncJobId, String cursorKey);
}
