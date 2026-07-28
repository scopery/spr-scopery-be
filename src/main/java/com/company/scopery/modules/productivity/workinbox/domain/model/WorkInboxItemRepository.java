package com.company.scopery.modules.productivity.workinbox.domain.model;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface WorkInboxItemRepository {
    WorkInboxItem save(WorkInboxItem item);
    Optional<WorkInboxItem> findByIdAndWorkspaceId(UUID id, UUID workspaceId);
    Optional<WorkInboxItem> findByIdAndUserId(UUID id, UUID userId);
    List<WorkInboxItem> findActiveByWorkspaceAndUser(UUID workspaceId, UUID userId);
    /** Personal inbox: all active items for user across workspaces. */
    List<WorkInboxItem> findActiveByUser(UUID userId);
    List<WorkInboxItem> findByUserIdAndSourceTypeAndSourceId(UUID userId, String sourceType, UUID sourceId);
    List<WorkInboxItem> findBySourceTypeAndSourceId(String sourceType, UUID sourceId);
    List<WorkInboxItem> findBySourceTypeInAndStatusIn(List<String> sourceTypes, List<String> statuses);
}
