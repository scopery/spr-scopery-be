package com.company.scopery.modules.integrationhub.sync.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SyncRunRepository {
    SyncRun save(SyncRun run);
    Optional<SyncRun> findById(UUID id);
    List<SyncRun> findByWorkspaceId(UUID workspaceId);
}
