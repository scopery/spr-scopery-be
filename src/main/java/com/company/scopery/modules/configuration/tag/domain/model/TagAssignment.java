package com.company.scopery.modules.configuration.tag.domain.model;
import java.time.Instant; import java.util.UUID;
public record TagAssignment(UUID id, UUID workspaceId, UUID tagDefinitionId, String objectTypeCode, UUID targetId,
        Instant archivedAt, int version, Instant createdAt, Instant updatedAt) {
    public static TagAssignment create(UUID workspaceId, UUID tagId, String objectType, UUID targetId) {
        Instant now = Instant.now();
        return new TagAssignment(UUID.randomUUID(), workspaceId, tagId, objectType, targetId, null, 0, now, now);
    }
    public TagAssignment archive() {
        return new TagAssignment(id, workspaceId, tagDefinitionId, objectTypeCode, targetId, Instant.now(), version, createdAt, Instant.now());
    }
}
