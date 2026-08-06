package com.company.scopery.modules.specpack.clarification.application.response;

import com.company.scopery.modules.specpack.clarification.domain.model.SpecPackClarification;

import java.time.Instant;
import java.util.UUID;

public record ClarificationResponse(
        UUID id,
        UUID sessionId,
        String code,
        String question,
        String answer,
        String priority,
        String status,
        String source,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
    public static ClarificationResponse from(SpecPackClarification clarification) {
        return new ClarificationResponse(
                clarification.id(),
                clarification.sessionId(),
                clarification.code(),
                clarification.question(),
                clarification.answer(),
                clarification.priority().name(),
                clarification.status().name(),
                clarification.source().name(),
                clarification.createdBy(),
                clarification.createdAt(),
                clarification.updatedAt()
        );
    }
}
