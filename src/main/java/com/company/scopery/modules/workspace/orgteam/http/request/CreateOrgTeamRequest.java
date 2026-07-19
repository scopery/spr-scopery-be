package com.company.scopery.modules.workspace.orgteam.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a new organization-level team")
public record CreateOrgTeamRequest(
        @Schema(description = "Display name of the organization team", example = "Platform Engineering")
        @NotBlank String name,

        @Schema(description = "Unique short code identifying the organization team (max 100 characters)", example = "PLATFORM-ENG")
        @NotBlank @Size(max = 100) String code,

        @Schema(description = "Optional description of the organization team", example = "Owns the platform and infrastructure", nullable = true)
        String description) {
}
