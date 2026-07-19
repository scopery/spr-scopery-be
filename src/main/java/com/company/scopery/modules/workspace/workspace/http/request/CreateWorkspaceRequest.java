package com.company.scopery.modules.workspace.workspace.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request payload for creating a new workspace")
public record CreateWorkspaceRequest(
        @Schema(description = "ID of the organization this workspace belongs to", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID organizationId,

        @Schema(description = "Display name of the workspace", example = "Engineering Hub")
        @NotBlank String name,

        @Schema(description = "Unique short code identifying the workspace", example = "ENG-HUB")
        @NotBlank String code,

        @Schema(description = "Optional description of the workspace", example = "Central hub for the engineering team", nullable = true)
        String description,

        @Schema(description = "Default visibility for the workspace", example = "PRIVATE", allowableValues = {"PUBLIC", "PRIVATE", "INTERNAL"}, nullable = true)
        String defaultVisibility,

        @Schema(description = "Join policy for the workspace", example = "INVITE_ONLY", allowableValues = {"OPEN", "INVITE_ONLY", "REQUEST_REQUIRED"}, nullable = true)
        String joinPolicy) {
}
