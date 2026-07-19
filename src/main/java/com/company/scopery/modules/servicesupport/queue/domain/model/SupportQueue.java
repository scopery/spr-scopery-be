package com.company.scopery.modules.servicesupport.queue.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SupportQueue(UUID id, UUID workspaceId, String queueCode, String name, String status, Instant createdAt) {
    public static SupportQueue create(UUID workspaceId, String queueCode, String name) {
        return new SupportQueue(UUID.randomUUID(), workspaceId, queueCode, name, "ACTIVE", Instant.now());
    }
    public SupportQueue deactivate() {
        return new SupportQueue(id, workspaceId, queueCode, name, "INACTIVE", createdAt);
    }
}
