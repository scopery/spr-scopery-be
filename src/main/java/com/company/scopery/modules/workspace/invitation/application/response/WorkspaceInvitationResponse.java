package com.company.scopery.modules.workspace.invitation.application.response;

import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceInvitationResponse(
        UUID id,
        UUID workspaceId,
        String invitedEmail,
        String invitationCode,
        String invitationCodeHint,
        String status,
        Integer maxUses,
        int usedCount,
        Instant expiresAt,
        Instant createdAt) {

    public static WorkspaceInvitationResponse from(WorkspaceInvitation inv, String rawCodeOnce) {
        return new WorkspaceInvitationResponse(
                inv.id(), inv.workspaceId(), inv.invitedEmail(),
                rawCodeOnce, inv.invitationCodeHint(), inv.status().name(),
                inv.maxUses(), inv.usedCount(), inv.expiresAt(), inv.createdAt());
    }

    public static WorkspaceInvitationResponse from(WorkspaceInvitation inv) {
        return from(inv, null);
    }
}
