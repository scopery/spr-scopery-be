package com.company.scopery.modules.workspace.workspace.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for updating an existing workspace")
public record UpdateWorkspaceRequest(
        @Schema(description = "New display name of the workspace", example = "Engineering Hub")
        @NotBlank String name,

        @Schema(description = "Updated description of the workspace", example = "Central hub for the engineering team", nullable = true)
        String description,

        @Schema(description = "Default visibility for the workspace", example = "PRIVATE", allowableValues = {"PUBLIC", "PRIVATE", "INTERNAL"}, nullable = true)
        String defaultVisibility,

        @Schema(description = "Join policy for the workspace", example = "INVITE_ONLY", allowableValues = {"OPEN", "INVITE_ONLY", "REQUEST_REQUIRED"}, nullable = true)
        String joinPolicy) {
}
