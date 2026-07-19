package com.company.scopery.modules.aiplanning.suggestion.application.response;

import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;

import java.time.Instant;
import java.util.UUID;

public record AiPlanningSuggestionResponse(
        UUID id, UUID planningRunId, UUID projectId, String suggestionType, String title,
        String summary, String rationale, String confidenceLabel, String status,
        Instant reviewedAt, UUID reviewedBy, Instant appliedAt, UUID appliedBy,
        Instant rejectedAt, UUID rejectedBy, String rejectionReason, int version,
        Instant createdAt, Instant updatedAt
) {
    public static AiPlanningSuggestionResponse from(AiPlanningSuggestion s) {
        return new AiPlanningSuggestionResponse(
                s.id(), s.planningRunId(), s.projectId(), s.suggestionType().name(), s.title(),
                s.summary(), s.rationale(), s.confidenceLabel(), s.status().name(),
                s.reviewedAt(), s.reviewedBy(), s.appliedAt(), s.appliedBy(),
                s.rejectedAt(), s.rejectedBy(), s.rejectionReason(), s.version(),
                s.createdAt(), s.updatedAt());
    }
}
