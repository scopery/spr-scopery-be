package com.company.scopery.modules.airecommendation.run.http.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateRecommendationRunRequest(
        @NotBlank String policyCode,
        List<String> packCodes,
        String triggerType,
        String idempotencyKey
) {}
