package com.company.scopery.modules.airecommendation.suggestion.http.request;

public record RejectSuggestionRequest(
        Long expectedVersion,
        String idempotencyKey,
        String reasonCode,
        String comment
) {}
