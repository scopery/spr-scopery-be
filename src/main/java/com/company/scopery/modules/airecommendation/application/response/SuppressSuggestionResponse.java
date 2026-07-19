package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SuppressSuggestionResponse(
        String suggestionRef,
        String status,
        UUID suppressionId,
        String scopeType,
        OffsetDateTime expiresAt,
        long version
) {}
