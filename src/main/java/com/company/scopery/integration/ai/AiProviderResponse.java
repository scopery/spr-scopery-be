package com.company.scopery.integration.ai;

import java.math.BigDecimal;

public record AiProviderResponse(
        String providerRequestId,
        String outputText,
        Integer inputTokenCount,
        Integer outputTokenCount,
        Integer totalTokenCount,
        BigDecimal estimatedCost
) {}