package com.company.scopery.modules.workspace.invitation.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

import java.time.Instant;

@Schema(description = "Request payload for creating a workspace invitation")
public record CreateWorkspaceInvitationRequest(
        @Schema(description = "Email address to restrict the invitation to a specific person; omit for open invitations", example = "jane.doe@example.com", nullable = true)
        @Email String invitedEmail,

        @Schema(description = "Maximum number of times this invitation can be used", example = "1", nullable = true)
        Integer maxUses,

        @Schema(description = "Timestamp when the invitation expires", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant expiresAt,

        @Schema(description = "Whether to send an email notification to the invitee", example = "true")
        boolean sendEmail) {}
