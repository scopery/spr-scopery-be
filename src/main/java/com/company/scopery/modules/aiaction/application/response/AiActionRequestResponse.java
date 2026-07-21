package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;
import java.util.UUID;

public record AiActionRequestResponse(
        UUID requestId,
        UUID workspaceId,
        UUID projectId,
        UUID initiatedByUserId,
        String originType,
        String originSuggestionRef,
        String status,
        String intentSummary,
        UUID latestPlanId,
        Instant createdAt,
        Instant updatedAt
) {}
