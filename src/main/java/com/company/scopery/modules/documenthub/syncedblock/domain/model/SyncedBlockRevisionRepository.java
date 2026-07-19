package com.company.scopery.modules.documenthub.syncedblock.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface SyncedBlockRevisionRepository {
    SyncedBlockRevision save(SyncedBlockRevision revision);
    Optional<SyncedBlockRevision> findBySyncedBlockIdAndRevisionNo(UUID syncedBlockId, long revisionNo);
    Optional<SyncedBlockRevision> findLatestBySyncedBlockId(UUID syncedBlockId);
}
