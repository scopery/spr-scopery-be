package com.company.scopery.modules.workspace.member.application.command;

import java.util.UUID;

public record DeactivateWorkspaceMemberCommand(
        UUID workspaceId,
        UUID memberId) {
}
