package com.company.scopery.modules.elicitation.round.application.response;

import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;

import java.time.Instant;
import java.util.UUID;

public record ElicitationRoundResponse(
        UUID id,
        UUID sessionId,
        int roundNumber,
        String questionsJson,
        String overallClarity,
        String status,
        Instant submittedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static ElicitationRoundResponse from(ElicitationRound round) {
        return new ElicitationRoundResponse(
                round.id(),
                round.sessionId(),
                round.roundNumber(),
                round.questionsJson(),
                round.overallClarity() != null ? round.overallClarity().name() : null,
                round.status().name(),
                round.submittedAt(),
                round.createdAt(),
                round.updatedAt()
        );
    }
}
