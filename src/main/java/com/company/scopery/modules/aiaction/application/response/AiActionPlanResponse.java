package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;
import java.util.UUID;

public record AiActionPlanResponse(
        UUID planId,
        UUID requestId,
        int planNumber,
        String status,
        String planHash,
        int version,
        String summary,
        String riskLevel,
        String executionMode,
        boolean requiresConfirmation,
        int stepCount,
        int targetCount,
        Instant expiresAt,
        Instant createdAt
) {}
