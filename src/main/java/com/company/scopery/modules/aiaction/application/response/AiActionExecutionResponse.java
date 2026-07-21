package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;
import java.util.UUID;

public record AiActionExecutionResponse(
        UUID executionId,
        UUID planId,
        String status,
        int executionVersion,
        Integer currentStepOrdinal,
        int succeededCount,
        int failedCount,
        int skippedCount,
        int compensatedCount,
        int cancelledCount,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt
) {}
