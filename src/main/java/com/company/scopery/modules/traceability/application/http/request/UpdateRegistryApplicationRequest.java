package com.company.scopery.modules.traceability.application.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRegistryApplicationRequest(
        @NotBlank @Size(max = 255) String name,
        String description
) {}
