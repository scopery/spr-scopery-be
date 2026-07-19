package com.company.scopery.modules.documenthub.suggestion.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentSuggestionOperation(
        UUID id,
        UUID suggestionId,
        String opType,
        String blockId,
        String path,
        String value,
        int ordinal,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentSuggestionOperation create(UUID suggestionId, String opType, String blockId,
                                                      String path, String value, int ordinal) {
        Instant now = Instant.now();
        return new DocumentSuggestionOperation(UUID.randomUUID(), suggestionId, opType, blockId, path, value, ordinal, now, now);
    }
}
