package com.company.scopery.modules.workspace.joinrequest.application.response;

import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceJoinRequestResponse(
        UUID id,
        UUID workspaceId,
        UUID requestedByUserId,
        String message,
        String status,
        UUID reviewedByUserId,
        Instant reviewedAt,
        String reviewNote,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceJoinRequestResponse from(WorkspaceJoinRequest r) {
        return new WorkspaceJoinRequestResponse(
                r.id(), r.workspaceId(), r.requestedByUserId(), r.message(),
                r.status().name(), r.reviewedByUserId(), r.reviewedAt(), r.reviewNote(),
                r.createdAt(), r.updatedAt());
    }
}
