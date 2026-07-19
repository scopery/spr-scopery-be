package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionFeedback;

import java.util.Optional;
import java.util.UUID;

public interface AiSuggestionFeedbackRepository {
    AiSuggestionFeedback save(AiSuggestionFeedback feedback);
    Optional<AiSuggestionFeedback> findBySuggestionAndActor(UUID suggestionId, UUID actorId);
}
