package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataSyncConflictJpaRepository extends JpaRepository<SyncConflictJpaEntity, UUID> {
    List<SyncConflictJpaEntity> findByWorkspaceId(UUID workspaceId);
}
