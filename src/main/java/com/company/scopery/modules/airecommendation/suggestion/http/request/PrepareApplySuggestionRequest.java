package com.company.scopery.modules.airecommendation.suggestion.http.request;

import java.util.List;
import java.util.UUID;

public record PrepareApplySuggestionRequest(
        Long expectedVersion,
        List<UUID> selectedItemIds,
        String idempotencyKey
) {}
