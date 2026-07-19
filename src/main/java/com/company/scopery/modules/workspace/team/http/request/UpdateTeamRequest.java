package com.company.scopery.modules.workspace.team.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for updating an existing workspace team")
public record UpdateTeamRequest(
        @Schema(description = "New display name of the team", example = "Backend Squad")
        @NotBlank String name,

        @Schema(description = "Updated description of the team", example = "Responsible for all backend services", nullable = true)
        String description) {
}
