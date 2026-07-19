package com.company.scopery.modules.documenthub.syncedblock.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SyncedBlockRepository {
    SyncedBlock save(SyncedBlock block);
    Optional<SyncedBlock> findById(UUID id);
    List<SyncedBlock> findByWorkspaceId(UUID workspaceId);
}
