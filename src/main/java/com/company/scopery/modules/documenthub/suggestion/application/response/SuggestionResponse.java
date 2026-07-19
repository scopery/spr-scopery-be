package com.company.scopery.modules.documenthub.suggestion.application.response;

import com.company.scopery.modules.documenthub.suggestion.domain.model.DocumentSuggestion;

import java.time.Instant;
import java.util.UUID;

public record SuggestionResponse(
        UUID id,
        UUID documentId,
        long targetRevisionNo,
        String description,
        String status,
        UUID acceptedBy,
        Instant acceptedAt,
        Long acceptedRevisionNo,
        UUID rejectedBy,
        Instant rejectedAt,
        Instant createdAt
) {
    public static SuggestionResponse from(DocumentSuggestion s) {
        return new SuggestionResponse(s.id(), s.documentId(), s.targetRevisionNo(), s.description(),
                s.status().name(), s.acceptedBy(), s.acceptedAt(), s.acceptedRevisionNo(),
                s.rejectedBy(), s.rejectedAt(), s.createdAt());
    }
}
