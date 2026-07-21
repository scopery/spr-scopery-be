package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAiActionStepJpaRepository extends JpaRepository<AiActionStepJpaEntity, UUID> {

    List<AiActionStepJpaEntity> findByPlanIdOrderByOrdinal(UUID planId);
}
