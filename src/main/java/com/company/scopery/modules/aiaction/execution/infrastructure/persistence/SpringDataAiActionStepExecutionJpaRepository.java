package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionStepExecutionJpaRepository extends JpaRepository<AiActionStepExecutionJpaEntity, UUID> {

    List<AiActionStepExecutionJpaEntity> findByExecutionIdOrderByOrdinal(UUID executionId);

    Optional<AiActionStepExecutionJpaEntity> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
