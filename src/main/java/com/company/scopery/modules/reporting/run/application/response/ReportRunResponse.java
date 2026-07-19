package com.company.scopery.modules.reporting.run.application.response;

import java.time.Instant;
import java.util.UUID;

public record ReportRunResponse(
        UUID id,
        UUID reportDefinitionId,
        UUID projectId,
        String status,
        String resultSummaryJson,
        String maskingSummaryJson,
        Instant completedAt
) {}
