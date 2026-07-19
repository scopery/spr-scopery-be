package com.company.scopery.modules.trust.classification.http.request;
import jakarta.validation.constraints.NotBlank;
public record UpsertDataClassificationPolicyRequest(@NotBlank String policyCode, @NotBlank String name,
        @NotBlank String defaultClassification, String description) {}
