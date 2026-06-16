package com.company.scopery.integration.ai;

import java.math.BigDecimal;
import java.util.UUID;

public record AiProviderRequest(
        UUID providerId,
        String model,
        String renderedPrompt,
        BigDecimal temperature,
        Integer maxOutputTokens
) {}
