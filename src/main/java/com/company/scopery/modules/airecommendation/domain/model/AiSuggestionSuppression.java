package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.SuppressionScopeType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AiSuggestionSuppression(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        SuppressionScopeType scopeType,
        String scopeKey,
        String suppressionKey,
        String targetEntityType,
        UUID targetEntityId,
        String suggestionType,
        String packCode,
        String reasonCode,
        String comment,
        boolean active,
        OffsetDateTime startsAt,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        long version
) {}
