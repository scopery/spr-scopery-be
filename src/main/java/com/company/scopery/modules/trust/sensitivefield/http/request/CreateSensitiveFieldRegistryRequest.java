package com.company.scopery.modules.trust.sensitivefield.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateSensitiveFieldRegistryRequest(@NotBlank String objectTypeCode, @NotBlank String fieldPath,
        String classification, String maskingStrategy) {}
