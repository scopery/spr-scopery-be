package com.company.scopery.integration.ai;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AiStreamingRequest(
        UUID providerId,
        String model,
        List<AiChatMessage> messages,
        BigDecimal temperature,
        Integer maxOutputTokens
) {}
