package com.company.scopery.modules.trust.sensitiveobject.http.request;
import jakarta.validation.constraints.NotBlank;
public record CreateSensitiveObjectRegistryRequest(@NotBlank String objectTypeCode, String classification) {}
