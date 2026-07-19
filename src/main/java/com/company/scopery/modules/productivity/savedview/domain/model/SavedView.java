package com.company.scopery.modules.productivity.savedview.domain.model;
import java.time.Instant; import java.util.UUID;
public record SavedView(UUID id, UUID workspaceId, UUID projectId, UUID ownerUserId, String targetType, String name,
        String viewConfigJson, String filtersJson, String sortJson, String columnsJson, String displayMode,
        String visibility, boolean defaultFlag, String status, int version, Instant createdAt, Instant updatedAt) {
    public static SavedView create(UUID workspaceId, UUID projectId, UUID ownerUserId, String targetType, String name,
                                   String viewConfigJson, String filtersJson) {
        Instant now = Instant.now();
        return new SavedView(UUID.randomUUID(), workspaceId, projectId, ownerUserId, targetType, name, viewConfigJson,
                filtersJson, null, null, null, "PRIVATE", false, "ACTIVE", 0, now, now);
    }
    public SavedView archive() {
        return new SavedView(id, workspaceId, projectId, ownerUserId, targetType, name, viewConfigJson, filtersJson,
                sortJson, columnsJson, displayMode, visibility, defaultFlag, "ARCHIVED", version, createdAt, Instant.now());
    }
}
