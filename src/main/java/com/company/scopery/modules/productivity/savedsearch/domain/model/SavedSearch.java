package com.company.scopery.modules.productivity.savedsearch.domain.model;
import java.time.Instant; import java.util.UUID;
public record SavedSearch(UUID id, UUID workspaceId, UUID projectId, UUID ownerUserId, String name, String scope, String queryText,
        String filtersJson, String sortJson, String visibility, String status, int version, Instant createdAt, Instant updatedAt) {
    public static SavedSearch create(UUID workspaceId, UUID projectId, UUID ownerUserId, String name, String scope, String queryText, String filtersJson) {
        Instant now = Instant.now();
        return new SavedSearch(UUID.randomUUID(), workspaceId, projectId, ownerUserId, name, scope, queryText, filtersJson, null, "PRIVATE", "ACTIVE", 0, now, now);
    }
    public SavedSearch archive() { return new SavedSearch(id, workspaceId, projectId, ownerUserId, name, scope, queryText, filtersJson, sortJson, visibility, "ARCHIVED", version, createdAt, Instant.now()); }
}
