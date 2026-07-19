package com.company.scopery.modules.airecommendation.application.compatibility;

import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceLabel;
import com.company.scopery.modules.airecommendation.domain.enums.ConfidenceMethod;
import com.company.scopery.modules.airecommendation.domain.enums.SourceSystem;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionSeverity;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class Phase21SuggestionCompatibilityMapper {

    public AiSuggestion toCommon(AiPlanningSuggestion p21) {
        OffsetDateTime createdAt = p21.createdAt() != null
                ? p21.createdAt().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime updatedAt = p21.updatedAt() != null
                ? p21.updatedAt().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime rejectedAt = p21.rejectedAt() != null
                ? p21.rejectedAt().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime acceptedAt = p21.reviewedAt() != null
                && isAccepted(p21) ? p21.reviewedAt().atOffset(ZoneOffset.UTC) : null;

        BigDecimal confidenceValue = mapConfidenceValue(p21.confidenceLabel());
        ConfidenceLabel confidenceLabel = mapConfidenceLabel(p21.confidenceLabel());

        return new AiSuggestion(
                p21.id(), null, null,
                p21.workspaceId(), p21.projectId(), SourceSystem.PHASE21, p21.id(),
                "PHASE21_PLANNING_COMPAT_V1", "PHASE21_PLANNING_COMPAT",
                p21.suggestionType() != null ? p21.suggestionType().name() : "PHASE21_PLANNING_PROPOSAL",
                "PHASE21_PLANNING_PROPOSAL", 1,
                "PLANNING", SuggestionSeverity.INFO, mapStatus(p21),
                p21.title(), p21.summary(), p21.rationale(),
                null, null, null,
                ConfidenceMethod.LEGACY_MAPPED, confidenceValue, confidenceLabel,
                "LOW", null, null, 1,
                null, null, null, null, null,
                createdAt, updatedAt,
                isViewed(p21) ? updatedAt : null,
                null, acceptedAt, rejectedAt, null, null,
                null, null, createdAt, updatedAt, (long) p21.version());
    }

    private SuggestionStatus mapStatus(AiPlanningSuggestion p21) {
        if (p21.status() == null) return SuggestionStatus.GENERATED;
        return switch (p21.status()) {
            case GENERATED -> SuggestionStatus.GENERATED;
            case UNDER_REVIEW -> SuggestionStatus.VIEWED;
            case ACCEPTED, PARTIALLY_ACCEPTED -> SuggestionStatus.ACCEPTED;
            case REJECTED -> SuggestionStatus.REJECTED;
            case APPLIED, PARTIALLY_APPLIED -> SuggestionStatus.ACCEPTED;
            case FAILED_TO_APPLY -> SuggestionStatus.REJECTED;
            case ARCHIVED -> SuggestionStatus.SUPPRESSED;
        };
    }

    private BigDecimal mapConfidenceValue(String label) {
        if (label == null) return new BigDecimal("0.5000");
        return switch (label.toUpperCase()) {
            case "HIGH" -> new BigDecimal("0.9000");
            case "MEDIUM" -> new BigDecimal("0.7000");
            default -> new BigDecimal("0.5000");
        };
    }

    private ConfidenceLabel mapConfidenceLabel(String label) {
        if (label == null) return ConfidenceLabel.MEDIUM;
        return switch (label.toUpperCase()) {
            case "HIGH" -> ConfidenceLabel.HIGH;
            case "LOW" -> ConfidenceLabel.LOW;
            default -> ConfidenceLabel.MEDIUM;
        };
    }

    private boolean isViewed(AiPlanningSuggestion p21) {
        return p21.status() != null && p21.status() !=
                com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionStatus.GENERATED;
    }

    private boolean isAccepted(AiPlanningSuggestion p21) {
        if (p21.status() == null) return false;
        return switch (p21.status()) {
            case ACCEPTED, PARTIALLY_ACCEPTED, APPLIED, PARTIALLY_APPLIED -> true;
            default -> false;
        };
    }
}
