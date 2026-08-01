package com.company.scopery.platform.bulkjob;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BulkJobService {

    private final BulkJobRepository repository;

    public BulkJobService(BulkJobRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public BulkJobRecord submit(String jobType, int totalItems, String payloadJson) {
        String actorUsername = resolveActorUsername();
        BulkJobRecord job = new BulkJobRecord(
                UUID.randomUUID(),
                jobType,
                BulkJobStatus.QUEUED,
                actorUsername,
                totalItems,
                0,
                0,
                payloadJson,
                null,
                null,
                List.of(),
                Instant.now(),
                Instant.now()
        );
        return repository.save(job);
    }

    @Transactional(readOnly = true)
    public Optional<BulkJobRecord> get(UUID id) {
        return repository.findById(id);
    }

    public List<BulkJobRecord> claimQueued(String workerId, int leaseSeconds, int limit) {
        return repository.claimQueued(workerId, leaseSeconds, limit);
    }

    public void markSucceeded(UUID id, String resultSummary) {
        repository.markSucceeded(id, resultSummary);
    }

    public void markPartial(UUID id, String resultSummary) {
        repository.markPartial(id, resultSummary);
    }

    public void markFailed(UUID id, String errorMessage) {
        repository.markFailed(id, errorMessage);
    }

    public void updateProgress(UUID id, int succeeded, int failed) {
        repository.updateProgress(id, succeeded, failed);
    }

    public void updateProgressWithFailure(UUID id, int succeeded, int failed, BulkJobFailure failure) {
        repository.updateProgressWithFailure(id, succeeded, failed, failure);
    }

    private String resolveActorUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return null;
    }
}
