package com.company.scopery.modules.workspace.orginvitation.application.command;

import java.time.Instant;
import java.util.UUID;

public record CreateOrgInvitationCommand(
        UUID organizationId,
        String inviteeEmail,
        String membershipType,
        Instant expiresAt) {
}
