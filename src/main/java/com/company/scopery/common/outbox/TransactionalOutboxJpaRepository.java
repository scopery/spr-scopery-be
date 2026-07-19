package com.company.scopery.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionalOutboxJpaRepository extends JpaRepository<TransactionalOutboxJpaEntity, UUID> {

    @Query(value = """
            SELECT id FROM app_transactional_outbox
            WHERE (
                    status = 'PENDING'
                    AND available_at <= :now
                    AND (next_retry_at IS NULL OR next_retry_at <= :now)
                  )
               OR (
                    status = 'PROCESSING'
                    AND locked_until IS NOT NULL
                    AND locked_until < :now
                  )
            ORDER BY occurred_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<UUID> findClaimableIds(@Param("now") Instant now, @Param("batchSize") int batchSize);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE app_transactional_outbox
            SET status = 'PROCESSING',
                locked_by = :lockedBy,
                locked_until = :lockedUntil,
                last_attempt_at = :now,
                updated_at = :now
            WHERE id = :id
              AND (
                    status = 'PENDING'
                    OR (status = 'PROCESSING' AND locked_until IS NOT NULL AND locked_until < :now)
                  )
            """, nativeQuery = true)
    int claim(@Param("id") UUID id,
              @Param("lockedBy") String lockedBy,
              @Param("lockedUntil") Instant lockedUntil,
              @Param("now") Instant now);
}
