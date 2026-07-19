package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestionSuppression;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AiSuggestionSuppressionRepository {
    AiSuggestionSuppression save(AiSuggestionSuppression suppression);
    Optional<AiSuggestionSuppression> findActiveByKey(UUID workspaceId, UUID projectId, UUID actorId, String suppressionKey);
    boolean hasActiveSuppressionFor(UUID workspaceId, UUID projectId, UUID actorId, String suggestionType, String targetEntityType, UUID targetEntityId);
    void deactivateExpired(OffsetDateTime before);
}
