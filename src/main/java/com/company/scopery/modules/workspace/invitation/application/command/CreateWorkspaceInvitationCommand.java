package com.company.scopery.modules.workspace.invitation.application.command;

import java.time.Instant;
import java.util.UUID;

public record CreateWorkspaceInvitationCommand(
        UUID workspaceId,
        String invitedEmail,
        Integer maxUses,
        Instant expiresAt,
        boolean sendEmail) {}
