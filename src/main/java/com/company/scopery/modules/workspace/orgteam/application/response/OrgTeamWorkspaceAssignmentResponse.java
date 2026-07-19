package com.company.scopery.modules.workspace.orgteam.application.response;

import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignment;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of an organization team's assignment to a workspace")
public record OrgTeamWorkspaceAssignmentResponse(
        @Schema(description = "Unique identifier of the assignment record", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the organization team", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID teamId,

        @Schema(description = "ID of the workspace the team is assigned to", example = "550e8400-e29b-41d4-a716-446655440002")
        UUID workspaceId,

        @Schema(description = "ID of the user who performed the assignment", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID assignedBy,

        @Schema(description = "Timestamp when the assignment was made", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant assignedAt,

        @Schema(description = "Current status of the assignment", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
        String status) {

    public static OrgTeamWorkspaceAssignmentResponse from(OrgTeamWorkspaceAssignment assignment) {
        return new OrgTeamWorkspaceAssignmentResponse(
                assignment.id(),
                assignment.teamId(),
                assignment.workspaceId(),
                assignment.assignedBy(),
                assignment.assignedAt(),
                assignment.status().name());
    }
}
