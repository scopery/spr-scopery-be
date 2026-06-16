package com.company.scopery.modules.workspace.member.application.command;

import java.util.UUID;

public record AddWorkspaceMemberCommand(
        UUID workspaceId,
        UUID userId) {
}
