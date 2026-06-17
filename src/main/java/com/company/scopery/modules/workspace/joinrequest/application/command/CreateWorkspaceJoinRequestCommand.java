package com.company.scopery.modules.workspace.joinrequest.application.command;

import java.util.UUID;

public record CreateWorkspaceJoinRequestCommand(
        UUID workspaceId,
        String workspaceCode,
        String message) {}
