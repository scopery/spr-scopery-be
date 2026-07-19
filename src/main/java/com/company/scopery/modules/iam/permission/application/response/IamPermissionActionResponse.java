package com.company.scopery.modules.iam.permission.application.response;

import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "An action definition within an IAM permission, describing a specific capability a subject can be granted")
public record IamPermissionActionResponse(
        @Schema(description = "Unique identifier of this permission action definition", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the parent permission this action belongs to", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID permissionId,

        @Schema(description = "Code of the parent permission (e.g. AI_PLATFORM_MANAGE)", example = "AI_PLATFORM_MANAGE")
        String permissionCode,

        @Schema(description = "Action code identifying the specific operation (e.g. READ, WRITE, EXECUTE)", example = "EXECUTE")
        String actionCode,

        @Schema(description = "Human-readable display name of this action", example = "Execute AI Agent")
        String name,

        @Schema(description = "Detailed description of what this action allows", example = "Allows triggering an AI agent execution", nullable = true)
        String description,

        @Schema(description = "ID of the legacy right that maps to this action for backward-compatibility", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID rightId,

        @Schema(description = "Legacy right code string for backward-compatibility", example = "AI_PLATFORM_MANAGE", nullable = true)
        String legacyRightCode,

        @Schema(description = "Current status of this action definition", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Timestamp when this action definition was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when this action definition was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static IamPermissionActionResponse from(IamPermissionActionDefinition action,
                                                   String permissionCode,
                                                   String legacyRightCode) {
        return new IamPermissionActionResponse(
                action.id(),
                action.permissionId(),
                permissionCode,
                action.actionCode(),
                action.name(),
                action.description(),
                action.rightId(),
                legacyRightCode,
                action.status().name(),
                action.createdAt(),
                action.updatedAt());
    }
}
