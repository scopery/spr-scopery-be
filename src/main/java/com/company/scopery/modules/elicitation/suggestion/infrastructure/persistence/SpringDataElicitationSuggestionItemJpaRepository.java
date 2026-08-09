package com.company.scopery.modules.elicitation.suggestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataElicitationSuggestionItemJpaRepository
        extends JpaRepository<ElicitationSuggestionItemJpaEntity, UUID> {

    List<ElicitationSuggestionItemJpaEntity> findBySuggestionIdOrderBySequenceAsc(UUID suggestionId);
}
