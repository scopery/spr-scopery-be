package com.company.scopery.modules.airecommendation.suggestion.http.request;

public record ViewSuggestionRequest(
        Long expectedVersion,
        String idempotencyKey
) {}
