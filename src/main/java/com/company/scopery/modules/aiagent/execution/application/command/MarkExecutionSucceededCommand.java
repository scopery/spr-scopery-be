package com.company.scopery.modules.aiagent.execution.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record MarkExecutionSucceededCommand(
        UUID id,
        Integer inputTokenCount,
        Integer outputTokenCount,
        Integer totalTokenCount,
        BigDecimal estimatedCost,
        String providerRequestId,
        String metadata
) {}