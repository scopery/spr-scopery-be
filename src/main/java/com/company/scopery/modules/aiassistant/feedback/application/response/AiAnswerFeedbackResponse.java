package com.company.scopery.modules.aiassistant.feedback.application.response;

import com.company.scopery.modules.aiassistant.domain.model.AiAnswerFeedback;

import java.time.Instant;
import java.util.UUID;

public record AiAnswerFeedbackResponse(
        UUID id,
        UUID messageId,
        UUID actorId,
        String rating,
        String reasonCode,
        Instant createdAt
) {
    public static AiAnswerFeedbackResponse from(AiAnswerFeedback f) {
        return new AiAnswerFeedbackResponse(
                f.id(),
                f.messageId(),
                f.actorId(),
                f.rating(),
                f.reasonCode(),
                f.createdAt()
        );
    }
}
