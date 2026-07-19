package com.company.scopery.modules.aiplanning.suggestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiPlanningSuggestionJpaRepository extends JpaRepository<AiPlanningSuggestionJpaEntity, UUID> {
    Optional<AiPlanningSuggestionJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<AiPlanningSuggestionJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<AiPlanningSuggestionJpaEntity> findByPlanningRunIdOrderByCreatedAtDesc(UUID planningRunId);
}
