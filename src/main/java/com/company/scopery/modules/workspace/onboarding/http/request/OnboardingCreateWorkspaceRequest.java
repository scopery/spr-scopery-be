package com.company.scopery.modules.workspace.onboarding.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for creating a new organization and workspace during onboarding")
public record OnboardingCreateWorkspaceRequest(
        @Schema(description = "Display name of the new organization", example = "Acme Corporation")
        @NotBlank String organizationName,

        @Schema(description = "Unique short code for the new organization", example = "ACME")
        @NotBlank String organizationCode,

        @Schema(description = "Display name of the new workspace", example = "Engineering Hub")
        @NotBlank String workspaceName,

        @Schema(description = "Unique short code for the new workspace", example = "ENG-HUB")
        @NotBlank String workspaceCode,

        @Schema(description = "Optional description of the new workspace", example = "Central hub for the engineering team", nullable = true)
        String workspaceDescription) {
}
