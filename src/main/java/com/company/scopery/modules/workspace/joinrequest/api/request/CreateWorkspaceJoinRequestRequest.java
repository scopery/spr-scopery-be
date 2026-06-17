package com.company.scopery.modules.workspace.joinrequest.api.request;

import java.util.UUID;

public record CreateWorkspaceJoinRequestRequest(
        UUID workspaceId,
        String workspaceCode,
        String message) {}
