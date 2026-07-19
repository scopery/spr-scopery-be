package com.company.scopery.common.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Claims and publishes transactional outbox rows with row-level locking and dead-letter support.
 * Does not send email directly — publishes {@link PlatformOutboxPublishedEvent} for consumers.
 */
@Component
public class TransactionalOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(TransactionalOutboxProcessor.class);

    private final TransactionalOutboxJpaRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;
    private final boolean enabled;
    private final int batchSize;
    private final int lockSeconds;
    private final int baseRetryDelaySeconds;
    private final String workerId;

    public TransactionalOutboxProcessor(TransactionalOutboxJpaRepository repository,
                                        ApplicationEventPublisher eventPublisher,
                                        PlatformTransactionManager transactionManager,
                                        @Value("${scopery.platform.outbox.enabled:true}") boolean enabled,
                                        @Value("${scopery.platform.outbox.batch-size:50}") int batchSize,
                                        @Value("${scopery.platform.outbox.lock-seconds:60}") int lockSeconds,
                                        @Value("${scopery.platform.outbox.base-retry-delay-seconds:30}") int baseRetryDelaySeconds) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.enabled = enabled;
        this.batchSize = batchSize;
        this.lockSeconds = lockSeconds;
        this.baseRetryDelaySeconds = baseRetryDelaySeconds;
        this.workerId = "outbox-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Scheduled(fixedDelayString = "${scopery.platform.outbox.fixed-delay-ms:5000}")
    public void processPending() {
        if (!enabled) {
            return;
        }
        Instant now = Instant.now();
        List<UUID> ids = transactionTemplate.execute(status -> repository.findClaimableIds(now, batchSize));
        if (ids == null || ids.isEmpty()) {
            return;
        }
        log.debug("[TransactionalOutboxProcessor] Claiming {} outbox rows", ids.size());
        for (UUID id : ids) {
            processOne(id);
        }
    }

    public void processOne(UUID id) {
        transactionTemplate.executeWithoutResult(status -> doProcessOne(id));
    }

    private void doProcessOne(UUID id) {
        Instant now = Instant.now();
        Instant lockedUntil = now.plus(lockSeconds, ChronoUnit.SECONDS);
        int claimed = repository.claim(id, workerId, lockedUntil, now);
        if (claimed == 0) {
            return;
        }

        TransactionalOutboxJpaEntity entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return;
        }

        try {
            eventPublisher.publishEvent(new PlatformOutboxPublishedEvent(
                    entity.getId(),
                    entity.getEventType(),
                    entity.getSourceSystem(),
                    entity.getEventVersion(),
                    entity.getAggregateType(),
                    entity.getAggregateId(),
                    entity.getPayload(),
                    entity.getTraceId()));

            entity.setStatus(OutboxStatus.PUBLISHED.name());
            entity.setPublishedAt(now);
            entity.setLockedBy(null);
            entity.setLockedUntil(null);
            entity.setErrorCode(null);
            entity.setErrorMessage(null);
            entity.setUpdatedAt(now);
            repository.saveAndFlush(entity);
        } catch (Exception exception) {
            handleFailure(entity, exception);
        }
    }

    private void handleFailure(TransactionalOutboxJpaEntity entity, Exception exception) {
        Instant now = Instant.now();
        int nextAttempt = entity.getRetryCount() + 1;
        entity.setRetryCount(nextAttempt);
        entity.setLastAttemptAt(now);
        entity.setErrorCode("PLATFORM_OUTBOX_PUBLISH_FAILED");
        entity.setErrorMessage(truncate(exception.getMessage(), 1000));
        entity.setUpdatedAt(now);

        if (nextAttempt >= entity.getMaxAttempts()) {
            entity.setStatus(OutboxStatus.DEAD_LETTER.name());
            entity.setLockedBy(null);
            entity.setLockedUntil(null);
            entity.setNextRetryAt(null);
            log.error("[TransactionalOutboxProcessor] Outbox {} moved to DEAD_LETTER after {} attempts",
                    entity.getId(), nextAttempt, exception);
        } else {
            long delaySeconds = (long) baseRetryDelaySeconds * (1L << Math.min(nextAttempt - 1, 6));
            entity.setStatus(OutboxStatus.PENDING.name());
            entity.setNextRetryAt(now.plus(delaySeconds, ChronoUnit.SECONDS));
            entity.setLockedBy(null);
            entity.setLockedUntil(null);
            log.warn("[TransactionalOutboxProcessor] Outbox {} failed (attempt {}/{}), retry at {}",
                    entity.getId(), nextAttempt, entity.getMaxAttempts(), entity.getNextRetryAt());
        }
        repository.saveAndFlush(entity);
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
