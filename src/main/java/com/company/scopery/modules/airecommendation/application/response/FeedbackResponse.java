package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedbackResponse(
        UUID feedbackId,
        String suggestionRef,
        OffsetDateTime createdAt
) {}
