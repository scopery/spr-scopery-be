package com.company.scopery.modules.traceability.componentoption.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateRegistryComponentOptionRequest(
        @NotBlank String optionValue,
        @NotBlank String optionLabel,
        int displayOrder
) {}
