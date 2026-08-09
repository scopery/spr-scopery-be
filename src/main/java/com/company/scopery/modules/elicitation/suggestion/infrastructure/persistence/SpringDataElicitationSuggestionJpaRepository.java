package com.company.scopery.modules.elicitation.suggestion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataElicitationSuggestionJpaRepository
        extends JpaRepository<ElicitationSuggestionJpaEntity, UUID> {

    Optional<ElicitationSuggestionJpaEntity> findByRoundId(UUID roundId);
}
