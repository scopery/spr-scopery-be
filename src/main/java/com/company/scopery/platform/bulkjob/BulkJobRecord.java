package com.company.scopery.platform.bulkjob;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BulkJobRecord(
        UUID id,
        String jobType,
        BulkJobStatus status,
        String actorUsername,
        int totalItems,
        int succeededItems,
        int failedItems,
        String payloadJson,
        String resultSummary,
        String errorMessage,
        List<BulkJobFailure> failures,
        Instant createdAt,
        Instant updatedAt
) {}
