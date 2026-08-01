package com.company.scopery.modules.traceability.appcomponent.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateRegistryAppComponentRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @Schema(description = "Free-form component type label, e.g. SERVICE, CONTROLLER, REPOSITORY, UI_COMPONENT") String componentType
) {}
