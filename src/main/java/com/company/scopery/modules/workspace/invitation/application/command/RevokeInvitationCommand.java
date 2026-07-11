package com.company.scopery.modules.workspace.invitation.application.command;

import java.util.UUID;

public record RevokeInvitationCommand(UUID id, UUID workspaceId) {}
