package com.company.scopery.modules.resourcereference.resourcetype.application.response;

import com.company.scopery.modules.resourcereference.resourcetype.domain.model.MentionResourceTypeDefinition;

import java.time.Instant;
import java.util.UUID;

public record ResourceTypeResponse(
        UUID id,
        String code,
        String displayName,
        String description,
        boolean isSystem,
        boolean enabled,
        Instant createdAt
) {
    public static ResourceTypeResponse from(MentionResourceTypeDefinition d) {
        return new ResourceTypeResponse(d.id(), d.code(), d.displayName(), d.description(),
                d.isSystem(), d.enabled(), d.createdAt());
    }
}
