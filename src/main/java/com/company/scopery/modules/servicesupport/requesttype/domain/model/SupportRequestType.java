package com.company.scopery.modules.servicesupport.requesttype.domain.model;
import java.time.Instant; import java.util.UUID;
public record SupportRequestType(UUID id, UUID workspaceId, String typeCode, String name, String description,
        String defaultPriority, boolean portalVisible, boolean enabled, String status,
        int version, Instant createdAt, Instant updatedAt) {
    public static SupportRequestType create(UUID workspaceId, String typeCode, String name, String defaultPriority, boolean portalVisible) {
        Instant now = Instant.now();
        return new SupportRequestType(UUID.randomUUID(), workspaceId, typeCode, name, null,
                defaultPriority, portalVisible, true, "ACTIVE", 0, now, now);
    }
    public SupportRequestType disable() {
        return new SupportRequestType(id, workspaceId, typeCode, name, description, defaultPriority,
                portalVisible, false, "INACTIVE", version, createdAt, Instant.now());
    }
    public SupportRequestType enable() {
        return new SupportRequestType(id, workspaceId, typeCode, name, description, defaultPriority,
                portalVisible, true, "ACTIVE", version, createdAt, Instant.now());
    }
}
