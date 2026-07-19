package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record SuggestionDetailResponse(
        SuggestionSummaryResponse suggestion,
        String reason,
        List<SuggestionItemResponse> items,
        List<EvidenceResponse> evidence,
        List<ImpactResponse> impacts,
        List<ReviewResponse> reviews,
        List<NextBestActionItemResponse> nextBestActions,
        Map<String, Object> legacyMetadata
) {

    public record SuggestionItemResponse(
            String itemId,
            int ordinal,
            String operation,
            String targetEntityType,
            String targetEntityId,
            String schemaCode,
            int schemaVersion,
            Map<String, Object> proposedPayload,
            String requiredTargetCapabilityCode,
            boolean confirmationRequired,
            String baselineImpact
    ) {}

    public record EvidenceResponse(
            String evidenceId,
            int ordinal,
            String evidenceType,
            String supportStrength,
            String sourceType,
            String sourceRefId,
            String title,
            String fragment,
            String appRoute,
            String accessValidationResult
    ) {}

    public record ImpactResponse(
            String dimension,
            String direction,
            String assessmentType,
            Double numericValue,
            String unitCode,
            String qualitativeMagnitude,
            String sourceMethod
    ) {}

    public record ReviewResponse(
            String reviewId,
            String actorId,
            String decision,
            String fromStatus,
            String toStatus,
            String reasonCode,
            String comment,
            OffsetDateTime createdAt
    ) {}
}
