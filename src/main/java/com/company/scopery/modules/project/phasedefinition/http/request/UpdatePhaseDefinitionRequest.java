package com.company.scopery.modules.project.phasedefinition.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePhaseDefinitionRequest(
        @NotBlank String name,
        String description,
        int displayOrder
) {}
