package com.company.scopery.modules.workspace.orgteam.application.response;

import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignment;

import java.time.Instant;
import java.util.UUID;

public record OrgTeamWorkspaceAssignmentResponse(
        UUID id,
        UUID teamId,
        UUID workspaceId,
        UUID assignedBy,
        Instant assignedAt,
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
