package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;
import java.util.UUID;

public record AiActionStepExecutionResponse(
        UUID stepExecutionId,
        UUID stepId,
        int ordinal,
        String toolCode,
        int attempt,
        String status,
        String resultVersionToken,
        String errorCode,
        Boolean retryable,
        Instant startedAt,
        Instant completedAt
) {}
