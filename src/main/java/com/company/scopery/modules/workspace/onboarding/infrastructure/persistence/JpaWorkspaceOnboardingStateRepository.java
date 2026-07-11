package com.company.scopery.modules.workspace.onboarding.infrastructure.persistence;

import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.onboarding.infrastructure.mapper.WorkspaceOnboardingStatePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWorkspaceOnboardingStateRepository implements WorkspaceOnboardingStateRepository {

    private final SpringDataWorkspaceOnboardingStateJpaRepository springDataRepository;
    private final WorkspaceOnboardingStatePersistenceMapper mapper;

    public JpaWorkspaceOnboardingStateRepository(
            SpringDataWorkspaceOnboardingStateJpaRepository springDataRepository,
            WorkspaceOnboardingStatePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceOnboardingState save(WorkspaceOnboardingState state) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(state)));
    }

    @Override
    public Optional<WorkspaceOnboardingState> findByUserId(UUID userId) {
        return springDataRepository.findByUserId(userId).map(mapper::toDomain);
    }
}
