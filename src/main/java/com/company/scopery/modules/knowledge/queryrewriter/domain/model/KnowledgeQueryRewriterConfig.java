package com.company.scopery.modules.knowledge.queryrewriter.domain.model;

import java.time.Instant;
import java.util.UUID;

public record KnowledgeQueryRewriterConfig(
        UUID id,
        UUID workspaceId,
        boolean enabled,
        String provider,
        String model,
        int maxTokens,
        String promptTemplate,
        long version,
        Instant createdAt,
        Instant updatedAt
) {}
