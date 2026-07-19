package com.company.scopery.modules.reporting.run.application.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record CreateReportRunResponse(
        UUID runId,
        String status,
        UUID snapshotId,
        JsonNode data,
        ReportMaskingSummaryResponse maskingSummary
) {}
