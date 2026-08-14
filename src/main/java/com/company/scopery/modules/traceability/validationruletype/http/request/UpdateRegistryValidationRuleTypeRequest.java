package com.company.scopery.modules.traceability.validationruletype.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRegistryValidationRuleTypeRequest(
        @NotBlank String name,
        @NotBlank String category,
        String paramSchemaJson,
        String defaultMessage,
        String description,
        int displayOrder
) {}
