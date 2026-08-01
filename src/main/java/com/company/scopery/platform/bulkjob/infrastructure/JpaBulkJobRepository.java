package com.company.scopery.platform.bulkjob.infrastructure;

import com.company.scopery.platform.bulkjob.BulkJobFailure;
import com.company.scopery.platform.bulkjob.BulkJobRecord;
import com.company.scopery.platform.bulkjob.BulkJobRepository;
import com.company.scopery.platform.bulkjob.BulkJobStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaBulkJobRepository implements BulkJobRepository {

    private static final TypeReference<List<BulkJobFailure>> FAILURE_LIST_TYPE = new TypeReference<>() {};

    private final SpringDataBulkJobJpaRepository springData;
    private final ObjectMapper objectMapper;

    public JpaBulkJobRepository(SpringDataBulkJobJpaRepository springData, ObjectMapper objectMapper) {
        this.springData = springData;
        this.objectMapper = objectMapper;
    }

    @Override
    public BulkJobRecord save(BulkJobRecord job) {
        BulkJobJpaEntity entity = toEntity(job);
        return toDomain(springData.saveAndFlush(entity));
    }

    @Override
    public Optional<BulkJobRecord> findById(UUID id) {
        return springData.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional
    public List<BulkJobRecord> claimQueued(String workerId, int leaseSeconds, int limit) {
        List<BulkJobJpaEntity> claimable = springData.findClaimableWithSkipLocked(limit);
        if (claimable.isEmpty()) {
            return List.of();
        }
        Instant now = Instant.now();
        for (BulkJobJpaEntity e : claimable) {
            e.setStatus(BulkJobStatus.RUNNING.name());
            e.setLeasedBy(workerId);
            e.setLeasedAt(now);
            e.setLeaseUntil(now.plusSeconds(leaseSeconds));
        }
        return springData.saveAllAndFlush(claimable).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void markRunning(UUID id, String workerId) {
        springData.findById(id).ifPresent(e -> {
            e.setStatus(BulkJobStatus.RUNNING.name());
            e.setLeasedBy(workerId);
            springData.saveAndFlush(e);
        });
    }

    @Override
    @Transactional
    public void updateProgress(UUID id, int succeeded, int failed) {
        springData.findById(id).ifPresent(e -> {
            e.setSucceededItems(succeeded);
            e.setFailedItems(failed);
            springData.saveAndFlush(e);
        });
    }

    @Override
    @Transactional
    public void updateProgressWithFailure(UUID id, int succeeded, int failed, BulkJobFailure failure) {
        springData.findById(id).ifPresent(e -> {
            e.setSucceededItems(succeeded);
            e.setFailedItems(failed);
            List<BulkJobFailure> existing = parseFailures(e.getFailuresJson());
            existing.add(failure);
            e.setFailuresJson(serializeFailures(existing));
            springData.saveAndFlush(e);
        });
    }

    @Override
    @Transactional
    public void markSucceeded(UUID id, String resultSummary) {
        springData.findById(id).ifPresent(e -> {
            e.setStatus(BulkJobStatus.SUCCEEDED.name());
            e.setSucceededItems(e.getTotalItems());
            e.setResultSummary(resultSummary);
            e.setLeasedBy(null);
            e.setLeasedAt(null);
            e.setLeaseUntil(null);
            springData.saveAndFlush(e);
        });
    }

    @Override
    @Transactional
    public void markPartial(UUID id, String resultSummary) {
        springData.findById(id).ifPresent(e -> {
            e.setStatus(BulkJobStatus.PARTIAL.name());
            e.setResultSummary(resultSummary);
            e.setLeasedBy(null);
            e.setLeasedAt(null);
            e.setLeaseUntil(null);
            springData.saveAndFlush(e);
        });
    }

    @Override
    @Transactional
    public void markFailed(UUID id, String errorMessage) {
        springData.findById(id).ifPresent(e -> {
            e.setStatus(BulkJobStatus.FAILED.name());
            e.setErrorMessage(errorMessage != null && errorMessage.length() > 2000
                    ? errorMessage.substring(0, 2000) : errorMessage);
            e.setLeasedBy(null);
            e.setLeasedAt(null);
            e.setLeaseUntil(null);
            springData.saveAndFlush(e);
        });
    }

    @Override
    @Transactional
    public void releaseLease(UUID id) {
        springData.findById(id).ifPresent(e -> {
            e.setLeasedBy(null);
            e.setLeasedAt(null);
            e.setLeaseUntil(null);
            springData.saveAndFlush(e);
        });
    }

    private BulkJobRecord toDomain(BulkJobJpaEntity e) {
        return new BulkJobRecord(
                (UUID) e.getId(),
                e.getJobType(),
                BulkJobStatus.valueOf(e.getStatus()),
                e.getActorUsername(),
                e.getTotalItems(),
                e.getSucceededItems(),
                e.getFailedItems(),
                e.getPayloadJson(),
                e.getResultSummary(),
                e.getErrorMessage(),
                parseFailures(e.getFailuresJson()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    private BulkJobJpaEntity toEntity(BulkJobRecord job) {
        BulkJobJpaEntity e = new BulkJobJpaEntity();
        e.setId(job.id());
        e.setJobType(job.jobType());
        e.setStatus(job.status().name());
        e.setActorUsername(job.actorUsername());
        e.setTotalItems(job.totalItems());
        e.setSucceededItems(job.succeededItems());
        e.setFailedItems(job.failedItems());
        e.setPayloadJson(job.payloadJson());
        e.setResultSummary(job.resultSummary());
        e.setErrorMessage(job.errorMessage());
        e.setFailuresJson("[]");
        if (job.createdAt() != null) {
            e.setCreatedAt(job.createdAt());
        }
        return e;
    }

    private List<BulkJobFailure> parseFailures(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, FAILURE_LIST_TYPE);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private String serializeFailures(List<BulkJobFailure> failures) {
        try {
            return objectMapper.writeValueAsString(failures);
        } catch (Exception ex) {
            return "[]";
        }
    }
}
