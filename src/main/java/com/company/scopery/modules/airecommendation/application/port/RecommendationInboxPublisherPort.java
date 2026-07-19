package com.company.scopery.modules.airecommendation.application.port;

import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;

import java.util.UUID;

public interface RecommendationInboxPublisherPort {
    void publish(AiSuggestion suggestion, UUID actorId);
    void close(UUID suggestionId, String reason);
}
