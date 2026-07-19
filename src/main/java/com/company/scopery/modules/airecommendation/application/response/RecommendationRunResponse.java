package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RecommendationRunResponse(
        UUID runId,
        UUID workspaceId,
        UUID projectId,
        String status,
        String triggerType,
        String policyCode,
        List<String> packCodes,
        RunCounts counts,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        Integer latencyMs,
        String error,
        String traceId
) {
    public record RunCounts(
            int detectors,
            int candidates,
            int persisted,
            int deduplicated,
            int suppressed,
            int discarded,
            int failedDetectors
    ) {}
}
