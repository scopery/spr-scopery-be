package com.company.scopery.modules.airecommendation.domain.model;

import com.company.scopery.modules.airecommendation.domain.enums.ReviewDecision;
import com.company.scopery.modules.airecommendation.domain.enums.SuggestionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AiSuggestionReview(
        UUID id,
        UUID suggestionId,
        UUID actorId,
        ReviewDecision decision,
        SuggestionStatus fromStatus,
        SuggestionStatus toStatus,
        long expectedSuggestionVersion,
        String reasonCode,
        String comment,
        String editedItemsJson,
        String traceId,
        OffsetDateTime createdAt
) {}
