package com.company.scopery.modules.elicitation.suggestion.application.response;

import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;

import java.time.Instant;
import java.util.UUID;

public record ElicitationSuggestionItemResponse(
        UUID id,
        UUID suggestionId,
        int sequence,
        String action,
        String targetEntityType,
        UUID targetEntityId,
        String targetEntityName,
        String rationale,
        String estimatedImpact,
        String status,
        String errorMessage,
        Instant executedAt,
        Instant createdAt
) {
    public static ElicitationSuggestionItemResponse from(ElicitationSuggestionItem item) {
        return new ElicitationSuggestionItemResponse(
                item.id(),
                item.suggestionId(),
                item.sequence(),
                item.action(),
                item.targetEntityType(),
                item.targetEntityId(),
                item.targetEntityName(),
                item.rationale(),
                item.estimatedImpact().name(),
                item.status().name(),
                item.errorMessage(),
                item.executedAt(),
                item.createdAt()
        );
    }
}
