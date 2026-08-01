package com.company.scopery.platform.bulkjob;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BulkJobRepository {

    BulkJobRecord save(BulkJobRecord job);

    Optional<BulkJobRecord> findById(UUID id);

    List<BulkJobRecord> claimQueued(String workerId, int leaseSeconds, int limit);

    void markRunning(UUID id, String workerId);

    void updateProgress(UUID id, int succeeded, int failed);

    void updateProgressWithFailure(UUID id, int succeeded, int failed, BulkJobFailure failure);

    void markSucceeded(UUID id, String resultSummary);

    void markPartial(UUID id, String resultSummary);

    void markFailed(UUID id, String errorMessage);

    void releaseLease(UUID id);
}
