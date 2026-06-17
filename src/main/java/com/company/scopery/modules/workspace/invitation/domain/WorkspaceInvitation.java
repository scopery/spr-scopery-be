package com.company.scopery.modules.workspace.invitation.domain;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceInvitation(
        UUID id,
        UUID workspaceId,
        UUID createdByUserId,
        String invitedEmail,
        String invitationCodeHash,
        String invitationCodeHint,
        WorkspaceInvitationStatus status,
        Integer maxUses,
        int usedCount,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceInvitation create(UUID workspaceId, UUID createdByUserId,
                                              String invitedEmail, Integer maxUses,
                                              Instant expiresAt, String rawCode) {
        Instant now = Instant.now();
        return new WorkspaceInvitation(
                UUID.randomUUID(), workspaceId, createdByUserId, invitedEmail,
                InvitationCodeHasher.hash(rawCode), InvitationCodeHasher.hint(rawCode),
                WorkspaceInvitationStatus.PENDING, maxUses, 0, expiresAt, now, now);
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isOverMaxUses() {
        return maxUses != null && usedCount >= maxUses;
    }

    public WorkspaceInvitation accept() {
        if (status != WorkspaceInvitationStatus.PENDING) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_REVOKED,
                    "Invitation is not pending", null);
        }
        if (isExpired()) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_EXPIRED,
                    "Invitation has expired", null);
        }
        if (isOverMaxUses()) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_MAX_USES_REACHED,
                    "Invitation has reached its maximum usage limit", null);
        }
        int newUsedCount = usedCount + 1;
        WorkspaceInvitationStatus newStatus = (maxUses != null && newUsedCount >= maxUses)
                ? WorkspaceInvitationStatus.ACCEPTED : WorkspaceInvitationStatus.PENDING;
        return new WorkspaceInvitation(id, workspaceId, createdByUserId, invitedEmail,
                invitationCodeHash, invitationCodeHint, newStatus, maxUses, newUsedCount,
                expiresAt, createdAt, Instant.now());
    }

    public WorkspaceInvitation revoke() {
        if (status != WorkspaceInvitationStatus.PENDING) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_INVITATION_REVOKED,
                    "Invitation is not pending", null);
        }
        return new WorkspaceInvitation(id, workspaceId, createdByUserId, invitedEmail,
                invitationCodeHash, invitationCodeHint, WorkspaceInvitationStatus.REVOKED,
                maxUses, usedCount, expiresAt, createdAt, Instant.now());
    }
}
