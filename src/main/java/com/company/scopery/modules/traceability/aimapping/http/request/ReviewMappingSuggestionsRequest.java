package com.company.scopery.modules.traceability.aimapping.http.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReviewMappingSuggestionsRequest(
        @NotEmpty @Valid List<SuggestionDecisionItem> decisions
) {
    public record SuggestionDecisionItem(
            @NotNull UUID suggestionId,
            @NotBlank String decision
    ) {}
}
