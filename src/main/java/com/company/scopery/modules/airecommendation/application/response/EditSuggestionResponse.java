package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;

public record EditSuggestionResponse(
        String suggestionRef,
        String status,
        OffsetDateTime editedAt,
        long version
) {}
