package com.company.scopery.modules.aiagent.execution.http.request;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateExecutionLogResultRequest(
        @PositiveOrZero Integer inputTokenCount,
        @PositiveOrZero Integer outputTokenCount,
        @PositiveOrZero Integer totalTokenCount,
        @PositiveOrZero BigDecimal estimatedCost,
        @Size(max = 255) String providerRequestId,
        @Size(max = 150) String errorCode,
        String errorMessage,
        String metadata
) {}
