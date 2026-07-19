package com.company.scopery.modules.aiplanning.suggestion.http.request;

import jakarta.validation.constraints.NotBlank;

public record RejectSuggestionRequest(@NotBlank String reason) {}
