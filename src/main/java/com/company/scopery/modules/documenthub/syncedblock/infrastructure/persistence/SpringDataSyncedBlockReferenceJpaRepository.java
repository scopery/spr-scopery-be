package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSyncedBlockReferenceJpaRepository extends JpaRepository<SyncedBlockReferenceJpaEntity, UUID> {
    Optional<SyncedBlockReferenceJpaEntity> findBySyncedBlockIdAndDocumentId(UUID syncedBlockId, UUID documentId);
    List<SyncedBlockReferenceJpaEntity> findBySyncedBlockId(UUID syncedBlockId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM SyncedBlockReferenceJpaEntity e WHERE e.documentId = :documentId AND e.syncedBlockId = :syncedBlockId")
    void deleteByDocumentIdAndSyncedBlockId(@Param("documentId") UUID documentId,
                                             @Param("syncedBlockId") UUID syncedBlockId);
}
