package com.company.scopery.modules.workspace.onboarding.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceOnboardingStateRepository {
    WorkspaceOnboardingState save(WorkspaceOnboardingState state);
    Optional<WorkspaceOnboardingState> findByUserId(UUID userId);
}
