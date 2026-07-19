package com.company.scopery.modules.iam.role.application.response;

import com.company.scopery.modules.iam.role.domain.model.IamRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "IAM role — a named collection of permissions that can be assigned to users or teams")
public record IamRoleResponse(
        @Schema(description = "Unique identifier of the role", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Unique machine-readable code for this role (e.g. ADMIN, AI_PLATFORM_OPERATOR)", example = "AI_PLATFORM_OPERATOR")
        String code,

        @Schema(description = "Human-readable display name of the role", example = "AI Platform Operator")
        String name,

        @Schema(description = "Detailed description of what this role grants", example = "Allows full operation of AI platform resources", nullable = true)
        String description,

        @Schema(description = "Current status of the role", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Scope of this role (SYSTEM = platform-wide; WORKSPACE = confined to a workspace)", example = "SYSTEM", allowableValues = {"SYSTEM", "WORKSPACE"}, nullable = true)
        String roleScope,

        @Schema(description = "Source of this role (e.g. BUILT_IN, CUSTOM)", example = "CUSTOM", nullable = true)
        String roleSource,

        @Schema(description = "Workspace this role is scoped to (only set when roleScope = WORKSPACE)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "ID of the parent role in the role hierarchy (null for root roles)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID parentRoleId,

        @Schema(description = "Whether this role is a built-in system role that cannot be deleted", example = "false")
        boolean isSystem,

        @Schema(description = "Timestamp when this role was soft-deleted (null if not deleted)", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant deletedAt,

        @Schema(description = "Timestamp when this role was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when this role was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static IamRoleResponse from(IamRole role) {
        return new IamRoleResponse(
                role.id(),
                role.code().value(),
                role.name(),
                role.description(),
                role.status().name(),
                role.roleScope() != null ? role.roleScope().name() : null,
                role.roleSource() != null ? role.roleSource().name() : null,
                role.workspaceId(),
                role.parentRoleId(),
                role.isSystem(),
                role.deletedAt(),
                role.createdAt(),
                role.updatedAt());
    }
}
