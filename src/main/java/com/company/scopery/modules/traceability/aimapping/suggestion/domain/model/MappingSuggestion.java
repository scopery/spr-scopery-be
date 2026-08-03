package com.company.scopery.modules.traceability.aimapping.suggestion.domain.model;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.ConfidenceBand;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionDecision;
import com.company.scopery.modules.traceability.aimapping.suggestion.domain.enums.SuggestionReviewStatus;
import com.company.scopery.modules.traceability.aimapping.summary.domain.enums.SummaryEntityType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MappingSuggestion(
        UUID id,
        UUID runId,
        SummaryEntityType sourceType,
        UUID sourceId,
        Integer sourceVersion,
        String sourceSummaryHash,
        SummaryEntityType targetType,
        UUID targetId,
        Integer targetVersion,
        String targetSummaryHash,
        MappingRelationType relationType,
        Integer rank,
        BigDecimal retrievalScore,
        BigDecimal aiScore,
        BigDecimal finalScore,
        BigDecimal secondBestScore,
        BigDecimal scoreMargin,
        ConfidenceBand confidenceBand,
        SuggestionDecision decision,
        String reasonCodesJson,
        String evidenceJson,
        String warningsJson,
        String coverageJson,
        SuggestionReviewStatus reviewStatus,
        UUID reviewedBy,
        Instant reviewedAt,
        Instant createdAt,
        Instant updatedAt
) {}
