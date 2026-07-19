package com.company.scopery.modules.workspace.team.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating a new workspace team")
public record CreateTeamRequest(
        @Schema(description = "Display name of the team", example = "Backend Squad")
        @NotBlank String name,

        @Schema(description = "Unique short code identifying the team within the workspace", example = "BE-SQUAD")
        @NotBlank String code,

        @Schema(description = "Optional description of the team", example = "Responsible for all backend services", nullable = true)
        String description) {
}
