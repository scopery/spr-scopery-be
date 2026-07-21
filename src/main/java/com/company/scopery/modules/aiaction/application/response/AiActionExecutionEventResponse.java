package com.company.scopery.modules.aiaction.application.response;

import java.time.Instant;

public record AiActionExecutionEventResponse(
        long sequence,
        int executionVersion,
        String eventType,
        Instant occurredAt,
        String payloadJson
) {}
