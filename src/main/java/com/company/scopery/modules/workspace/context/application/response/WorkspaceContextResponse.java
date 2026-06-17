package com.company.scopery.modules.workspace.context.application.response;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceContextResponse(
        UUID userId,
        UUID currentWorkspaceId,
        String currentWorkspaceName,
        String currentWorkspaceCode,
        Instant lastSwitchedAt,
        boolean onboardingCompleted) {}
