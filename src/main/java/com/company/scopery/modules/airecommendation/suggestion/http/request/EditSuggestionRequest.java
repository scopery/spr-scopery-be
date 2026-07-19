package com.company.scopery.modules.airecommendation.suggestion.http.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EditSuggestionRequest(
        @NotNull Long expectedVersion,
        String idempotencyKey,
        List<EditItemRequest> items,
        String comment
) {
    public record EditItemRequest(
            @NotNull UUID itemId,
            Map<String, Object> proposedPayload
    ) {}
}
