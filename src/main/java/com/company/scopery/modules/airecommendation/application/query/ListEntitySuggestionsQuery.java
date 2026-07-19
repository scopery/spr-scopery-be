package com.company.scopery.modules.airecommendation.application.query;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record ListEntitySuggestionsQuery(
        UUID workspaceId,
        UUID actorId,
        String entityType,
        UUID entityId,
        UUID projectId,
        boolean includeLegacyPhase21,
        Pageable pageable
) {}
