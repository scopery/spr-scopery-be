package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;

public record SuggestionSummaryResponse(
        String suggestionRef,
        String sourceSystem,
        String projectId,
        String type,
        String category,
        String severity,
        String status,
        String title,
        String summary,
        TargetInfo target,
        ConfidenceInfo confidence,
        String riskLevel,
        int occurrenceCount,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        long version
) {
    public record TargetInfo(String entityType, String entityId, String versionToken, String appRoute) {}
    public record ConfidenceInfo(String method, double value, String label) {}
}
