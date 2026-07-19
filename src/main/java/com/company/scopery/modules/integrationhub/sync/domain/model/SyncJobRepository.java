package com.company.scopery.modules.integrationhub.sync.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SyncJobRepository {
    SyncJob save(SyncJob j);
    Optional<SyncJob> findById(UUID id);
    List<SyncJob> findByWorkspaceId(UUID workspaceId);
}
