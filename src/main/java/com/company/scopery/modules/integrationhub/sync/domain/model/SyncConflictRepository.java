package com.company.scopery.modules.integrationhub.sync.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SyncConflictRepository {
    SyncConflict save(SyncConflict c);
    Optional<SyncConflict> findById(UUID id);
    List<SyncConflict> findByWorkspaceId(UUID workspaceId);
}
