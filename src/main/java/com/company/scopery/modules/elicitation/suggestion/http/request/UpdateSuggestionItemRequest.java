package com.company.scopery.modules.elicitation.suggestion.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSuggestionItemRequest(@NotBlank String changesJson) {}
