package com.company.scopery.modules.traceability.aimapping.application.command;

import java.util.List;
import java.util.UUID;

public record ReviewSuggestionsCommand(
        UUID projectId,
        List<SuggestionDecision> decisions,
        UUID reviewedBy
) {
    public record SuggestionDecision(UUID suggestionId, String decision) {}
}
