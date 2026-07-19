package com.company.scopery.modules.airecommendation.suggestion.http.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SuppressSuggestionRequest(
        @NotNull Long expectedVersion,
        String idempotencyKey,
        @NotBlank String scopeType,
        @Min(1) @Max(90) int durationDays,
        String reasonCode,
        String comment
) {}
