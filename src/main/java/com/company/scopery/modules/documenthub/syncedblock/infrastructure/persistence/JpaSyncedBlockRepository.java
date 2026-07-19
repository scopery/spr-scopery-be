package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlock;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRepository;
import com.company.scopery.modules.documenthub.syncedblock.infrastructure.mapper.SyncedBlockPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSyncedBlockRepository implements SyncedBlockRepository {

    private final SpringDataSyncedBlockJpaRepository springData;
    private final SyncedBlockPersistenceMapper mapper;

    public JpaSyncedBlockRepository(SpringDataSyncedBlockJpaRepository springData,
                                     SyncedBlockPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public SyncedBlock save(SyncedBlock block) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(block)));
    }

    @Override
    public Optional<SyncedBlock> findById(UUID id) {
        return springData.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<SyncedBlock> findByWorkspaceId(UUID workspaceId) {
        return springData.findByWorkspaceId(workspaceId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
