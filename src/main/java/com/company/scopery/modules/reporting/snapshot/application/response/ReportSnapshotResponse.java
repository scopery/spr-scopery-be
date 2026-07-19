package com.company.scopery.modules.reporting.snapshot.application.response;

import java.util.UUID;

public record ReportSnapshotResponse(
        UUID id,
        UUID reportRunId,
        String snapshotType,
        String dataJson,
        String maskingSummaryJson
) {}
