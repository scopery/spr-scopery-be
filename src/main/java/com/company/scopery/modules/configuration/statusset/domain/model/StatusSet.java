package com.company.scopery.modules.configuration.statusset.domain.model;
import java.time.Instant; import java.util.UUID;
public record StatusSet(UUID id, UUID workspaceId, String objectTypeCode, String setCode, String name, String status, int version, Instant createdAt, Instant updatedAt) {
    public static StatusSet create(UUID workspaceId, String objectType, String setCode, String name) {
        Instant now = Instant.now();
        return new StatusSet(UUID.randomUUID(), workspaceId, objectType, setCode, name, "DRAFT", 0, now, now);
    }
}
