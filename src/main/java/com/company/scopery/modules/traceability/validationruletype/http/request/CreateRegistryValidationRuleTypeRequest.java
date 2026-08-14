package com.company.scopery.modules.traceability.validationruletype.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRegistryValidationRuleTypeRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotBlank String category,
        String paramSchemaJson,
        String defaultMessage,
        String description,
        int displayOrder
) {}
