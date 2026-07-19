package com.company.scopery.modules.configuration.layout.domain.model;
import java.time.Instant; import java.util.UUID;
public record LayoutDefinition(UUID id, UUID workspaceId, String objectTypeCode, String layoutType, String name, String layoutJson,
        String status, boolean currentFlag, int version, Instant createdAt, Instant updatedAt) {
    public static LayoutDefinition create(UUID workspaceId, String objectType, String layoutType, String name, String layoutJson) {
        Instant now = Instant.now();
        return new LayoutDefinition(UUID.randomUUID(), workspaceId, objectType, layoutType, name, layoutJson, "DRAFT", false, 0, now, now);
    }
    public LayoutDefinition publish() {
        return new LayoutDefinition(id, workspaceId, objectTypeCode, layoutType, name, layoutJson, "PUBLISHED", true, version, createdAt, Instant.now());
    }
}
