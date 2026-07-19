package com.company.scopery.modules.airecommendation.application.response;

import java.time.OffsetDateTime;

public record RejectSuggestionResponse(
        String suggestionRef,
        String status,
        OffsetDateTime rejectedAt,
        long version
) {}
