package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataSyncedBlockRevisionJpaRepository extends JpaRepository<SyncedBlockRevisionJpaEntity, UUID> {
    Optional<SyncedBlockRevisionJpaEntity> findBySyncedBlockIdAndRevisionNo(UUID syncedBlockId, long revisionNo);
    Optional<SyncedBlockRevisionJpaEntity> findFirstBySyncedBlockIdOrderByRevisionNoDesc(UUID syncedBlockId);
}
