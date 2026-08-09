package com.company.scopery.modules.elicitation.suggestion.application.response;

import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestion;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ElicitationSuggestionResponse(
        UUID id,
        UUID roundId,
        String overallSummary,
        String status,
        Instant createdAt,
        List<ElicitationSuggestionItemResponse> items
) {
    public static ElicitationSuggestionResponse from(ElicitationSuggestion suggestion,
                                                       List<ElicitationSuggestionItem> items) {
        return new ElicitationSuggestionResponse(
                suggestion.id(),
                suggestion.roundId(),
                suggestion.overallSummary(),
                suggestion.status().name(),
                suggestion.createdAt(),
                items.stream().map(ElicitationSuggestionItemResponse::from).toList()
        );
    }
}
