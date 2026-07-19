package com.company.scopery.modules.airecommendation.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AiSuggestionFeedback(
        UUID id,
        UUID suggestionId,
        UUID actorId,
        Boolean helpful,
        String reasonCode,
        String comment,
        String observedOutcome,
        OffsetDateTime createdAt
) {}
