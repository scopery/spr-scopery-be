package com.company.scopery.platform.bulkjob;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BulkJobResponse(
        UUID id,
        String jobType,
        @Schema(allowableValues = {"QUEUED", "RUNNING", "SUCCEEDED", "PARTIAL", "FAILED"}) String status,
        int totalItems,
        int succeededItems,
        int failedItems,
        String resultSummary,
        String errorMessage,
        @Schema(description = "Per-item failures. Empty while SUCCEEDED with no errors. Populated incrementally during RUNNING and complete on PARTIAL/FAILED.")
        List<BulkJobFailure> failures,
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
                job.failures() != null ? job.failures() : List.of(),
                job.createdAt(),
                job.updatedAt()
        );
    }
}
