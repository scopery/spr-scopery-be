package com.company.scopery.modules.aiassistant.message.application.response;

import java.util.UUID;

public record AiSseStartResponse(
        UUID conversationId,
        UUID userMessageId,
        UUID assistantMessageId,
        UUID turnId,
        String streamUrl
) {}
