package com.company.scopery.modules.workspace.team.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
        @NotBlank String name,
        @NotBlank String code,
        String description) {
}
