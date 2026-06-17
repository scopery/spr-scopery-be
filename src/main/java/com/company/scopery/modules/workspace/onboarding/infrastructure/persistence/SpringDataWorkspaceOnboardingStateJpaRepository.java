package com.company.scopery.modules.workspace.onboarding.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataWorkspaceOnboardingStateJpaRepository
        extends JpaRepository<WorkspaceOnboardingStateJpaEntity, UUID> {

    Optional<WorkspaceOnboardingStateJpaEntity> findByUserId(UUID userId);
}
