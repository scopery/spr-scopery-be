package com.company.scopery.modules.knowledge.indexing.application.response;

import java.time.Instant;
import java.util.UUID;

public record IndexJobResponse(
        UUID jobId,
        UUID workspaceId,
        UUID projectId,
        UUID sourceId,
        String jobType,
        String jobStatus,
        String idempotencyKey,
        int processedCount,
        int successCount,
        int failureCount,
        Instant queuedAt
) {}
