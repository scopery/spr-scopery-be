package com.company.scopery.platform.bulkjob;

import java.time.Instant;
import java.util.UUID;

public record BulkJobResponse(
        UUID id,
        String jobType,
        String status,
        int totalItems,
        int succeededItems,
        int failedItems,
        String resultSummary,
        String errorMessage,
        Instant createdAt,
        Instant updatedAt
) {
    public static BulkJobResponse from(BulkJobRecord job) {
        return new BulkJobResponse(
                job.id(),
                job.jobType(),
                job.status().name(),
                job.totalItems(),
                job.succeededItems(),
                job.failedItems(),
                job.resultSummary(),
                job.errorMessage(),
                job.createdAt(),
                job.updatedAt()
        );
    }
}
