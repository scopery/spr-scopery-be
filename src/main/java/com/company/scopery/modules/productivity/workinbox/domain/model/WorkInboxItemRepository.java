package com.company.scopery.modules.productivity.workinbox.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface WorkInboxItemRepository {
    WorkInboxItem save(WorkInboxItem item);
    Optional<WorkInboxItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    List<WorkInboxItem> findActiveByWorkspaceAndUser(UUID workspaceId, UUID userId);
}
