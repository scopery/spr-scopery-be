package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSyncRunJpaRepository extends JpaRepository<SyncRunJpaEntity, UUID> {
    List<SyncRunJpaEntity> findByWorkspaceId(UUID workspaceId);
}
