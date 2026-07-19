package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;

import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRun;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncRunRepository;
import com.company.scopery.modules.integrationhub.sync.infrastructure.mapper.SyncPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSyncRunRepository implements SyncRunRepository {
    private final SpringDataSyncRunJpaRepository spring;
    private final SyncPersistenceMapper mapper;

    public JpaSyncRunRepository(SpringDataSyncRunJpaRepository spring, SyncPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public SyncRun save(SyncRun run) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(run)));
    }

    @Override
    public Optional<SyncRun> findById(UUID id) {
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SyncRun> findByWorkspaceId(UUID workspaceId) {
        return spring.findByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }
}
