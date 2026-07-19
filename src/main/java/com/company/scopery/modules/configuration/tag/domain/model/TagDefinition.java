package com.company.scopery.modules.configuration.tag.domain.model;
import java.time.Instant; import java.util.UUID;
public record TagDefinition(UUID id, UUID workspaceId, String tagCode, String label, String color, String allowedObjectTypesJson,
        String status, int version, Instant createdAt, Instant updatedAt) {
    public static TagDefinition create(UUID workspaceId, String code, String label, String color, String allowedTypes) {
        Instant now = Instant.now();
        return new TagDefinition(UUID.randomUUID(), workspaceId, code, label, color, allowedTypes, "ACTIVE", 0, now, now);
    }
    public TagDefinition archive() {
        return new TagDefinition(id, workspaceId, tagCode, label, color, allowedObjectTypesJson, "ARCHIVED", version, createdAt, Instant.now());
    }
}
