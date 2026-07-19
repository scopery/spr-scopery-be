package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiSuggestionFeedbackJpaRepository
        extends JpaRepository<AiSuggestionFeedbackJpaEntity, UUID> {

    Optional<AiSuggestionFeedbackJpaEntity> findBySuggestionIdAndActorId(UUID suggestionId, UUID actorId);
}
