package com.company.scopery.modules.workspace.member.application.command;

import java.util.UUID;

public record ActivateWorkspaceMemberCommand(
        UUID workspaceId,
        UUID memberId) {
}
