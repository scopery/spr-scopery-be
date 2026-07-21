package com.company.scopery.modules.aiaction.realtime.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionExecutionEventJpaRepository extends JpaRepository<AiActionExecutionEventJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(e.sequence), 0) FROM AiActionExecutionEventJpaEntity e WHERE e.executionId = :executionId")
    long findMaxSequenceByExecutionId(@Param("executionId") UUID executionId);

    @Query("SELECT e FROM AiActionExecutionEventJpaEntity e WHERE e.executionId = :executionId AND e.sequence > :afterSequence ORDER BY e.sequence ASC")
    List<AiActionExecutionEventJpaEntity> findByExecutionIdAndSequenceGreaterThan(
            @Param("executionId") UUID executionId,
            @Param("afterSequence") long afterSequence);

    @Query("SELECT e FROM AiActionExecutionEventJpaEntity e WHERE e.redisPublishedAt IS NULL AND e.occurredAt > :since ORDER BY e.occurredAt ASC")
    List<AiActionExecutionEventJpaEntity> findUnpublishedSince(@Param("since") Instant since);

    @Query("SELECT MIN(e.sequence) FROM AiActionExecutionEventJpaEntity e WHERE e.executionId = :executionId")
    Optional<Long> findMinSequenceByExecutionId(@Param("executionId") UUID executionId);
}
