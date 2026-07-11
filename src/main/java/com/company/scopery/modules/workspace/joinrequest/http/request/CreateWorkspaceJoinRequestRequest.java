package com.company.scopery.modules.workspace.joinrequest.http.request;

import java.util.UUID;

public record CreateWorkspaceJoinRequestRequest(
        UUID workspaceId,
        String workspaceCode,
        String message) {}
