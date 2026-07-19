package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;

public record ViewSuggestionResponse(
        String suggestionRef,
        String status,
        OffsetDateTime viewedAt,
        long version
) {}
