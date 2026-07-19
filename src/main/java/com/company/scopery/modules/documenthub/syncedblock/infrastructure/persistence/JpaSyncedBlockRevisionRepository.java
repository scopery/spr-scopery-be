package com.company.scopery.modules.documenthub.syncedblock.infrastructure.persistence;

import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRevision;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRevisionRepository;
import com.company.scopery.modules.documenthub.syncedblock.infrastructure.mapper.SyncedBlockPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaSyncedBlockRevisionRepository implements SyncedBlockRevisionRepository {

    private final SpringDataSyncedBlockRevisionJpaRepository springData;
    private final SyncedBlockPersistenceMapper mapper;

    public JpaSyncedBlockRevisionRepository(SpringDataSyncedBlockRevisionJpaRepository springData,
                                              SyncedBlockPersistenceMapper mapper) {
        this.springData = springData;
        this.mapper = mapper;
    }

    @Override
    public SyncedBlockRevision save(SyncedBlockRevision revision) {
        return mapper.toDomain(springData.saveAndFlush(mapper.toJpaEntity(revision)));
    }

    @Override
    public Optional<SyncedBlockRevision> findBySyncedBlockIdAndRevisionNo(UUID syncedBlockId, long revisionNo) {
        return springData.findBySyncedBlockIdAndRevisionNo(syncedBlockId, revisionNo).map(mapper::toDomain);
    }

    @Override
    public Optional<SyncedBlockRevision> findLatestBySyncedBlockId(UUID syncedBlockId) {
        return springData.findFirstBySyncedBlockIdOrderByRevisionNoDesc(syncedBlockId).map(mapper::toDomain);
    }
}
