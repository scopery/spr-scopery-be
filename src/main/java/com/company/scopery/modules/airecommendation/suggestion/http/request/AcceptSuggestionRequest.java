package com.company.scopery.modules.airecommendation.suggestion.http.request;

public record AcceptSuggestionRequest(
        Long expectedVersion,
        String idempotencyKey,
        String comment
) {}
