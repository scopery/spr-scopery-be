package com.company.scopery.modules.resourcereference.resourcetype.domain.model;

import java.time.Instant;
import java.util.UUID;

public record MentionResourceTypeDefinition(
        UUID id,
        String code,
        String displayName,
        String description,
        boolean isSystem,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
    public static MentionResourceTypeDefinition create(String code, String displayName,
                                                        String description, boolean isSystem) {
        Instant now = Instant.now();
        return new MentionResourceTypeDefinition(UUID.randomUUID(), code, displayName,
                description, isSystem, true, now, now);
    }

    public MentionResourceTypeDefinition disable() {
        return new MentionResourceTypeDefinition(id, code, displayName, description,
                isSystem, false, createdAt, Instant.now());
    }

    public MentionResourceTypeDefinition enable() {
        return new MentionResourceTypeDefinition(id, code, displayName, description,
                isSystem, true, createdAt, Instant.now());
    }
}
