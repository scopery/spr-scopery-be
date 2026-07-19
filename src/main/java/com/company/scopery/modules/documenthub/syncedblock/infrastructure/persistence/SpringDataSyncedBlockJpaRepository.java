package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSyncedBlockJpaRepository extends JpaRepository<SyncedBlockJpaEntity, UUID> {
    List<SyncedBlockJpaEntity> findByWorkspaceId(UUID workspaceId);
}
