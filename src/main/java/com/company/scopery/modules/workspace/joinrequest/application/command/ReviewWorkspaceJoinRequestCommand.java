package com.company.scopery.modules.workspace.joinrequest.application.command;

import java.util.UUID;

public record ReviewWorkspaceJoinRequestCommand(UUID id, UUID workspaceId, String reviewNote) {}
