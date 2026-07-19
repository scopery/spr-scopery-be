package com.company.scopery.modules.integrationhub.sync.infrastructure.persistence;

import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursor;
import com.company.scopery.modules.integrationhub.sync.domain.model.SyncCursorRepository;
import com.company.scopery.modules.integrationhub.sync.infrastructure.mapper.SyncPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSyncCursorRepository implements SyncCursorRepository {
    private final SpringDataSyncCursorJpaRepository spring;
    private final SyncPersistenceMapper mapper;

    public JpaSyncCursorRepository(SpringDataSyncCursorJpaRepository spring, SyncPersistenceMapper mapper) {
        this.spring = spring;
        this.mapper = mapper;
    }

    @Override
    public SyncCursor save(SyncCursor cursor) {
        return mapper.toDomain(spring.saveAndFlush(mapper.toJpaEntity(cursor)));
    }

    @Override
    public Optional<SyncCursor> findBySyncJobIdAndCursorKey(UUID syncJobId, String cursorKey) {
        return spring.findBySyncJobIdAndCursorKey(syncJobId, cursorKey).map(mapper::toDomain);
    }
}
