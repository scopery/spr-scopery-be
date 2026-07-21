package com.company.scopery.modules.aiaction.execution.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAiActionCompensationJpaRepository extends JpaRepository<AiActionCompensationJpaEntity, UUID> {

    List<AiActionCompensationJpaEntity> findByExecutionId(UUID executionId);
}
