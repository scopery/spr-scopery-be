package com.company.scopery.modules.airecommendation.infrastructure.inbox;

import com.company.scopery.modules.airecommendation.application.port.RecommendationInboxPublisherPort;
import com.company.scopery.modules.airecommendation.domain.model.AiSuggestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkInboxRecommendationPublisherAdapter implements RecommendationInboxPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(WorkInboxRecommendationPublisherAdapter.class);

    @Override
    public void publish(AiSuggestion suggestion, UUID actorId) {
        log.debug("WorkInboxRecommendationPublisherAdapter: publish stub for suggestion={} severity={}",
                suggestion.id(), suggestion.severity());
    }

    @Override
    public void close(UUID suggestionId, String reason) {
        log.debug("WorkInboxRecommendationPublisherAdapter: close stub for suggestion={} reason={}", suggestionId, reason);
    }
}
