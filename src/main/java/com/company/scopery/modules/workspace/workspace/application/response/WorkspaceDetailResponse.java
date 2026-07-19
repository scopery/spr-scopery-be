package com.company.scopery.modules.workspace.workspace.application.response;

import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Detailed representation of a workspace, returned after creation")
public record WorkspaceDetailResponse(
        @Schema(description = "Unique identifier of the workspace", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the organization this workspace belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID organizationId,

        @Schema(description = "Unique short code of the workspace", example = "ENG-HUB")
        String code,

        @Schema(description = "Display name of the workspace", example = "Engineering Hub")
        String name,

        @Schema(description = "Description of the workspace", example = "Central hub for the engineering team", nullable = true)
        String description,

        @Schema(description = "User ID of the workspace owner", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID ownerUserId,

        @Schema(description = "Default visibility of the workspace", example = "PRIVATE", allowableValues = {"PUBLIC", "PRIVATE", "INTERNAL"})
        String defaultVisibility,

        @Schema(description = "Join policy of the workspace", example = "INVITE_ONLY", allowableValues = {"OPEN", "INVITE_ONLY", "REQUEST_REQUIRED"})
        String joinPolicy,

        @Schema(description = "Current status of the workspace", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "ARCHIVED"})
        String status,

        @Schema(description = "Timestamp when the workspace was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the workspace was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt,

        @Schema(description = "Whether the owner membership record was automatically created", example = "true")
        boolean ownerMembershipCreated) {

    public static WorkspaceDetailResponse from(Workspace ws, boolean ownerMembershipCreated) {
        return new WorkspaceDetailResponse(
                ws.id(),
                ws.organizationId(),
                ws.code().value(),
                ws.name(),
                ws.description(),
                ws.ownerUserId(),
                ws.defaultVisibility().name(),
                ws.joinPolicy() != null ? ws.joinPolicy().name() : "INVITE_ONLY",
                ws.status().name(),
                ws.createdAt(),
                ws.updatedAt(),
                ownerMembershipCreated);
    }
}
