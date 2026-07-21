package com.company.scopery.modules.aiaction.execution.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActionStepExecutionRepository {

    AiActionStepExecution save(AiActionStepExecution stepExecution);

    Optional<AiActionStepExecution> findById(UUID id);

    List<AiActionStepExecution> findByExecutionIdOrderByOrdinal(UUID executionId);

    Optional<AiActionStepExecution> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);
}
