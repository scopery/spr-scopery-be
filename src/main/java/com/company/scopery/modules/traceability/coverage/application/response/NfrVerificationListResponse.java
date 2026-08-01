package com.company.scopery.modules.traceability.coverage.application.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record NfrVerificationListResponse(
        List<NfrVerificationItem> items,
        PageInfo page,
        Instant generatedAt
) {
    public record NfrVerificationItem(
            UUID requirementId,
            String code,
            String title,
            String qualityAttribute,
            long targetCount,
            long verificationCaseCount,
            String latestMeasurement,
            String latestResult,
            String coverageStatus,
            String nextAction
    ) {}

    public record PageInfo(int limit, int offset, long total) {}
}
