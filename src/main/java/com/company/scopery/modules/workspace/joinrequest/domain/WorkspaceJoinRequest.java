package com.company.scopery.modules.workspace.joinrequest.domain;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceJoinRequest(
        UUID id,
        UUID workspaceId,
        UUID requestedByUserId,
        String message,
        WorkspaceJoinRequestStatus status,
        UUID reviewedByUserId,
        Instant reviewedAt,
        String reviewNote,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceJoinRequest create(UUID workspaceId, UUID requestedByUserId, String message) {
        Instant now = Instant.now();
        return new WorkspaceJoinRequest(UUID.randomUUID(), workspaceId, requestedByUserId, message,
                WorkspaceJoinRequestStatus.PENDING, null, null, null, now, now);
    }

    public WorkspaceJoinRequest approve(UUID reviewedByUserId) {
        assertPending();
        return new WorkspaceJoinRequest(id, workspaceId, requestedByUserId, message,
                WorkspaceJoinRequestStatus.APPROVED, reviewedByUserId, Instant.now(), null,
                createdAt, Instant.now());
    }

    public WorkspaceJoinRequest reject(UUID reviewedByUserId, String reviewNote) {
        assertPending();
        return new WorkspaceJoinRequest(id, workspaceId, requestedByUserId, message,
                WorkspaceJoinRequestStatus.REJECTED, reviewedByUserId, Instant.now(), reviewNote,
                createdAt, Instant.now());
    }

    public WorkspaceJoinRequest cancel() {
        assertPending();
        return new WorkspaceJoinRequest(id, workspaceId, requestedByUserId, message,
                WorkspaceJoinRequestStatus.CANCELLED, null, null, null, createdAt, Instant.now());
    }

    private void assertPending() {
        if (status != WorkspaceJoinRequestStatus.PENDING) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_PENDING,
                    "Join request is no longer pending", null);
        }
    }
}
