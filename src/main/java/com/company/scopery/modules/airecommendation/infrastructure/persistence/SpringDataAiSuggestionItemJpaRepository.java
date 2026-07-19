package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAiSuggestionItemJpaRepository
        extends JpaRepository<AiSuggestionItemJpaEntity, UUID> {

    List<AiSuggestionItemJpaEntity> findBySuggestionIdOrderByOrdinal(UUID suggestionId);
}
