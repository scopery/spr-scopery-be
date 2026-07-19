package com.company.scopery.modules.workspace.orgteam.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for updating an existing organization-level team")
public record UpdateOrgTeamRequest(
        @Schema(description = "New display name of the organization team", example = "Platform Engineering")
        @NotBlank String name,

        @Schema(description = "Updated description of the organization team", example = "Owns the platform and infrastructure", nullable = true)
        String description) {
}
