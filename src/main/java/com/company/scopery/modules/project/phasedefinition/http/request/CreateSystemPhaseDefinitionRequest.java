package com.company.scopery.modules.project.phasedefinition.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateSystemPhaseDefinitionRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        int displayOrder,
        boolean isSystemDefault
) {}
