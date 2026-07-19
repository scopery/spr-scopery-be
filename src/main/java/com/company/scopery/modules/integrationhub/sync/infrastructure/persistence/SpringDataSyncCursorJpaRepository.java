package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataSyncCursorJpaRepository extends JpaRepository<SyncCursorJpaEntity, UUID> {
    Optional<SyncCursorJpaEntity> findBySyncJobIdAndCursorKey(UUID syncJobId, String cursorKey);
}
