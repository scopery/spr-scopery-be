package com.company.scopery.modules.airecommendation.application.command;

import com.company.scopery.modules.airecommendation.domain.enums.SuppressionScopeType;

import java.util.UUID;

public record SuppressSuggestionCommand(
        UUID workspaceId,
        UUID actorId,
        String suggestionRef,
        long expectedVersion,
        String idempotencyKey,
        SuppressionScopeType scopeType,
        int durationDays,
        String reasonCode,
        String comment,
        String traceId
) {}
