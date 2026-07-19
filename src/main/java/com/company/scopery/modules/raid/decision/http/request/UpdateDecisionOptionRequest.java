package com.company.scopery.modules.raid.decision.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateDecisionOptionRequest(
        @NotBlank String optionTitle,
        String optionDescription,
        String pros,
        String cons,
        String estimatedImpact
) {}
