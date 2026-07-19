package com.company.scopery.platform.web.idempotency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class IdempotencyCleanupJob {

    private final IdempotencyKeyJpaRepository repository;
    private final boolean enabled;

    public IdempotencyCleanupJob(IdempotencyKeyJpaRepository repository,
                                 @Value("${scopery.platform.idempotency.cleanup-enabled:true}") boolean enabled) {
        this.repository = repository;
        this.enabled = enabled;
    }

    @Scheduled(cron = "${scopery.platform.idempotency.cleanup-cron:0 15 * * * *}")
    @Transactional
    public void deleteExpiredRecords() {
        if (!enabled) {
            return;
        }
        repository.deleteExpired(Instant.now());
    }
}
