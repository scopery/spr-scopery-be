package com.company.scopery.modules.aiplanning.suggestionitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiPlanningSuggestionItemJpaRepository extends JpaRepository<AiPlanningSuggestionItemJpaEntity, UUID> {
    List<AiPlanningSuggestionItemJpaEntity> findBySuggestionIdOrderByCreatedAtAsc(UUID suggestionId);
    Optional<AiPlanningSuggestionItemJpaEntity> findByIdAndSuggestionId(UUID id, UUID suggestionId);
}
