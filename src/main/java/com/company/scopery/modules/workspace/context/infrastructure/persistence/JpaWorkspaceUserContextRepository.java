package com.company.scopery.modules.workspace.context.infrastructure.persistence;

import com.company.scopery.modules.workspace.context.domain.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.context.infrastructure.mapper.WorkspaceUserContextPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaWorkspaceUserContextRepository implements WorkspaceUserContextRepository {

    private final SpringDataWorkspaceUserContextJpaRepository springDataRepository;
    private final WorkspaceUserContextPersistenceMapper mapper;

    public JpaWorkspaceUserContextRepository(SpringDataWorkspaceUserContextJpaRepository springDataRepository,
                                              WorkspaceUserContextPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceUserContext save(WorkspaceUserContext context) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toJpaEntity(context)));
    }

    @Override
    public Optional<WorkspaceUserContext> findByUserId(UUID userId) {
        return springDataRepository.findById(userId).map(mapper::toDomain);
    }
}
