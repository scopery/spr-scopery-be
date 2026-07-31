package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GapsResponse(
        GapSummary summary,
        List<GapItem> items,
        PageInfo page,
        Instant generatedAt,
        boolean stale
) {
    public record GapSummary(long total, Map<String, Long> byGapCode) {}

    public record GapItem(
            String id,
            String gapCode,
            String priority,
            RequirementRef requirement,
            TraceabilityMatrixResponse.PreviewObject relatedObject,
            String recommendedAction,
            String message
    ) {}

    public record RequirementRef(UUID id, String code, String title) {}

    public record PageInfo(int limit, int offset, long total) {}
}
