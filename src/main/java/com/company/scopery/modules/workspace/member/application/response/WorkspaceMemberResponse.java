package com.company.scopery.modules.workspace.member.application.response;

import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a workspace member")
public record WorkspaceMemberResponse(
        @Schema(description = "Unique identifier of the membership record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "ID of the member user", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID userId,

        @Schema(description = "Current membership status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status,

        @Schema(description = "Timestamp when the user joined the workspace", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant joinedAt,

        @Schema(description = "Timestamp when the membership record was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the membership record was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static WorkspaceMemberResponse from(WorkspaceMember m) {
        return new WorkspaceMemberResponse(
                m.id(),
                m.workspaceId(),
                m.userId(),
                m.status().name(),
                m.joinedAt(),
                m.createdAt(),
                m.updatedAt());
    }
}
