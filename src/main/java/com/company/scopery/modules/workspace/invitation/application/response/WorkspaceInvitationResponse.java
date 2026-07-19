package com.company.scopery.modules.workspace.invitation.application.response;

import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a workspace invitation")
public record WorkspaceInvitationResponse(
        @Schema(description = "Unique identifier of the invitation", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID of the workspace this invitation belongs to", example = "550e8400-e29b-41d4-a716-446655440001")
        UUID workspaceId,

        @Schema(description = "Email address the invitation is restricted to, or null for open invitations", example = "jane.doe@example.com", nullable = true)
        String invitedEmail,

        @Schema(description = "Raw invitation code returned only once at creation time", example = "abc123xyz", nullable = true)
        String invitationCode,

        @Schema(description = "Masked hint of the invitation code for display", example = "abc1***", nullable = true)
        String invitationCodeHint,

        @Schema(description = "Current status of the invitation", example = "PENDING")
        String status,

        @Schema(description = "Maximum number of times this invitation can be used", example = "1", nullable = true)
        Integer maxUses,

        @Schema(description = "Number of times this invitation has been used", example = "0")
        int usedCount,

        @Schema(description = "Timestamp when the invitation expires", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant expiresAt,

        @Schema(description = "Timestamp when the invitation was created", example = "2026-07-17T03:00:00.000000Z")
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
