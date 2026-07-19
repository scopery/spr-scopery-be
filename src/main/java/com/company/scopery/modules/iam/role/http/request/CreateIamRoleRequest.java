package com.company.scopery.modules.iam.role.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request to create a new IAM role that can be assigned to users or teams")
public record CreateIamRoleRequest(
        @Schema(description = "Unique machine-readable code for this role (2–100 characters, e.g. AI_PLATFORM_OPERATOR)", example = "AI_PLATFORM_OPERATOR")
        @NotBlank @Size(min = 2, max = 100) String code,

        @Schema(description = "Human-readable display name of the role (max 255 characters)", example = "AI Platform Operator")
        @NotBlank @Size(max = 255) String name,

        @Schema(description = "Optional description of what this role grants (max 2000 characters)", example = "Allows full operation of AI platform resources", nullable = true)
        @Size(max = 2000) String description,

        @Schema(description = "Scope of this role (SYSTEM = platform-wide; WORKSPACE = confined to a workspace)", example = "SYSTEM", allowableValues = {"SYSTEM", "WORKSPACE"}, nullable = true)
        String roleScope,

        @Schema(description = "Source of this role (e.g. BUILT_IN, CUSTOM)", example = "CUSTOM", nullable = true)
        String roleSource,

        @Schema(description = "Workspace to scope this role to (required when roleScope = WORKSPACE)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "ID of a parent role to inherit from in the role hierarchy", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID parentRoleId) {}
