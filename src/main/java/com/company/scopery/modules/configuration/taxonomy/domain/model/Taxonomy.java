package com.company.scopery.modules.configuration.taxonomy.domain.model;
import java.time.Instant; import java.util.UUID;
public record Taxonomy(UUID id, UUID workspaceId, String taxonomyCode, String name, String status, int version, Instant createdAt, Instant updatedAt) {
    public static Taxonomy create(UUID workspaceId, String code, String name) {
        Instant now = Instant.now();
        return new Taxonomy(UUID.randomUUID(), workspaceId, code, name, "ACTIVE", 0, now, now);
    }
}
