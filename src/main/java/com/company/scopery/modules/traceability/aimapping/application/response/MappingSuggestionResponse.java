package com.company.scopery.modules.traceability.aimapping.application.response;

import com.company.scopery.modules.traceability.aimapping.suggestion.domain.model.MappingSuggestion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MappingSuggestionResponse(
        UUID id,
        UUID runId,
        String sourceType,
        UUID sourceId,
        Integer sourceVersion,
        String targetType,
        UUID targetId,
        Integer targetVersion,
        String relationType,
        Integer rank,
        BigDecimal finalScore,
        BigDecimal scoreMargin,
        String confidenceBand,
        String decision,
        String reasonCodesJson,
        String evidenceJson,
        String warningsJson,
        String reviewStatus,
        UUID reviewedBy,
        Instant reviewedAt,
        Instant createdAt
) {
    public static MappingSuggestionResponse from(MappingSuggestion s) {
        return new MappingSuggestionResponse(
                s.id(),
                s.runId(),
                s.sourceType().name(),
                s.sourceId(),
                s.sourceVersion(),
                s.targetType() != null ? s.targetType().name() : null,
                s.targetId(),
                s.targetVersion(),
                s.relationType().name(),
                s.rank(),
                s.finalScore(),
                s.scoreMargin(),
                s.confidenceBand() != null ? s.confidenceBand().name() : null,
                s.decision().name(),
                s.reasonCodesJson(),
                s.evidenceJson(),
                s.warningsJson(),
                s.reviewStatus().name(),
                s.reviewedBy(),
                s.reviewedAt(),
                s.createdAt()
        );
    }
}
