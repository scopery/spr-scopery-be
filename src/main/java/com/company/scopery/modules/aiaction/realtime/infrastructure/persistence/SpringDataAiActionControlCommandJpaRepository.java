package com.company.scopery.modules.aiaction.realtime.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionControlCommandJpaRepository extends JpaRepository<AiActionControlCommandJpaEntity, UUID> {

    Optional<AiActionControlCommandJpaEntity> findByIdempotencyKey(String idempotencyKey);

    List<AiActionControlCommandJpaEntity> findByExecutionIdAndStatusOrderByCreatedAtAsc(UUID executionId, String status);
}
