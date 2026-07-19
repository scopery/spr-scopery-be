package com.company.scopery.modules.aiplanning.planningrun.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiPlanningRunJpaRepository extends JpaRepository<AiPlanningRunJpaEntity, UUID> {
    Optional<AiPlanningRunJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<AiPlanningRunJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
