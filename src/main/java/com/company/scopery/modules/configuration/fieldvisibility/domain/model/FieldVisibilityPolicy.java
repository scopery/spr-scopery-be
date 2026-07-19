package com.company.scopery.modules.configuration.fieldvisibility.domain.model;
import java.time.Instant; import java.util.UUID;
public record FieldVisibilityPolicy(UUID id, UUID workspaceId, UUID customFieldDefinitionId, String audienceType, boolean visible, Instant createdAt, Instant updatedAt) {
    public static FieldVisibilityPolicy create(UUID workspaceId, UUID fieldId, String audienceType, boolean visible) {
        Instant now = Instant.now();
        return new FieldVisibilityPolicy(UUID.randomUUID(), workspaceId, fieldId, audienceType, visible, now, now);
    }
    public FieldVisibilityPolicy withVisible(boolean visible) {
        return new FieldVisibilityPolicy(id, workspaceId, customFieldDefinitionId, audienceType, visible, createdAt, Instant.now());
    }
}
