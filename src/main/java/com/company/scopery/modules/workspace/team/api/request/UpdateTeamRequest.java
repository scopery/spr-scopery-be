package com.company.scopery.modules.workspace.team.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateTeamRequest(
        @NotBlank String name,
        String description) {
}
