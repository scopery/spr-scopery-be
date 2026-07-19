package com.company.scopery.modules.aiplanning.suggestion.http.request;

public record ApplySuggestionRequest(
        String applyMode,
        Boolean requireChangeRequestIfBaselined,
        String idempotencyKey
) {}
