package com.company.scopery.modules.workspace.context.domain.model;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceUserContext(
        UUID userId,
        UUID currentWorkspaceId,
        Instant lastSwitchedAt,
        Instant onboardingCompletedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceUserContext create(UUID userId) {
        Instant now = Instant.now();
        return new WorkspaceUserContext(userId, null, null, null, now, now);
    }

    public WorkspaceUserContext switchTo(UUID workspaceId) {
        return new WorkspaceUserContext(userId, workspaceId, Instant.now(),
                onboardingCompletedAt, createdAt, Instant.now());
    }

    public WorkspaceUserContext completeOnboarding() {
        return new WorkspaceUserContext(userId, currentWorkspaceId, lastSwitchedAt,
                Instant.now(), createdAt, Instant.now());
    }
}
