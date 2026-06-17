package com.company.scopery.modules.workspace.invitation.api.request;

import jakarta.validation.constraints.Email;

import java.time.Instant;

public record CreateWorkspaceInvitationRequest(
        @Email String invitedEmail,
        Integer maxUses,
        Instant expiresAt,
        boolean sendEmail) {}
