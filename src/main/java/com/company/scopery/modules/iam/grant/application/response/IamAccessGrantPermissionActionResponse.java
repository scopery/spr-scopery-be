package com.company.scopery.modules.iam.grant.application.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "A permission-action entry that is part of an IAM access grant, linking a grant to a specific capability and optional legacy right")
public record IamAccessGrantPermissionActionResponse(
        @Schema(description = "ID of the parent access grant", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID grantId,

        @Schema(description = "ID of the resource this permission action applies to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID resourceId,

        @Schema(description = "Workspace scope of this permission action", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "ID of the permission action definition", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID permissionActionId,

        @Schema(description = "ID of the parent permission", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID permissionId,

        @Schema(description = "Code of the permission (e.g. AI_PLATFORM_MANAGE)", example = "AI_PLATFORM_MANAGE", nullable = true)
        String permissionCode,

        @Schema(description = "Action code within the permission (e.g. READ, WRITE, EXECUTE)", example = "EXECUTE", nullable = true)
        String actionCode,

        @Schema(description = "ID of the legacy right associated with this permission action", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID rightId,

        @Schema(description = "Legacy right code for backward-compatibility", example = "AI_PLATFORM_MANAGE", nullable = true)
        String legacyRightCode,

        @Schema(description = "Timestamp when this permission action entry was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt) {
}
