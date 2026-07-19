package com.company.scopery.modules.aiassistant.message.application.response;

import com.company.scopery.modules.aiassistant.domain.model.AiMessage;

import java.time.Instant;
import java.util.UUID;

public record AiMessageResponse(
        UUID id,
        UUID conversationId,
        UUID turnId,
        int sequenceInConversation,
        String role,
        String status,
        String content,
        String responseMode,
        int inputTokenCount,
        int outputTokenCount,
        String errorCode,
        Instant createdAt,
        Instant completedAt
) {
    public static AiMessageResponse from(AiMessage m) {
        return new AiMessageResponse(
                m.id(),
                m.conversationId(),
                m.turnId(),
                m.sequenceInConversation(),
                m.role().name(),
                m.status().name(),
                m.content(),
                m.responseMode() != null ? m.responseMode().name() : null,
                m.inputTokenCount(),
                m.outputTokenCount(),
                m.errorCode(),
                m.createdAt(),
                m.completedAt()
        );
    }
}
