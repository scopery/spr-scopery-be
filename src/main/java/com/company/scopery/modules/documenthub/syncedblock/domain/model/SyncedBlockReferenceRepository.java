package com.company.scopery.modules.documenthub.syncedblock.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SyncedBlockReferenceRepository {
    SyncedBlockReference save(SyncedBlockReference ref);
    Optional<SyncedBlockReference> findBySyncedBlockIdAndDocumentId(UUID syncedBlockId, UUID documentId);
    List<SyncedBlockReference> findBySyncedBlockId(UUID syncedBlockId);
    void deleteByDocumentIdAndSyncedBlockId(UUID documentId, UUID syncedBlockId);
}
