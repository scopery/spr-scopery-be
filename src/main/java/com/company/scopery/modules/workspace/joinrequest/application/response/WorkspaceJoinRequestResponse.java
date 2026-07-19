package com.company.scopery.modules.workspace.joinrequest.application.response;

import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a workspace join request")
public record WorkspaceJoinRequestResponse(
        @Schema(description = "Unique identifier of the join request", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace the request is for", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "ID of the user who submitted the join request", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID requestedByUserId,

        @Schema(description = "Message provided by the requester", example = "I would like to join to contribute to the backend services.", nullable = true)
        String message,

        @Schema(description = "Current status of the join request", example = "PENDING")
        String status,

        @Schema(description = "ID of the user who reviewed the request", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID reviewedByUserId,

        @Schema(description = "Timestamp when the request was reviewed", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant reviewedAt,

        @Schema(description = "Note from the reviewer", example = "Welcome to the team!", nullable = true)
        String reviewNote,

        @Schema(description = "Timestamp when the join request was created", example = "2026-07-17T03:00:00.000000Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the join request was last updated", example = "2026-07-17T03:00:00.000000Z")
        Instant updatedAt) {

    public static WorkspaceJoinRequestResponse from(WorkspaceJoinRequest r) {
        return new WorkspaceJoinRequestResponse(
                r.id(), r.workspaceId(), r.requestedByUserId(), r.message(),
                r.status().name(), r.reviewedByUserId(), r.reviewedAt(), r.reviewNote(),
                r.createdAt(), r.updatedAt());
    }
}
